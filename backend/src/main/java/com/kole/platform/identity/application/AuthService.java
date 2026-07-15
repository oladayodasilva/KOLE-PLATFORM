package com.kole.platform.identity.application;

import com.kole.platform.common.exception.ConflictException;
import com.kole.platform.common.exception.ErrorCode;
import com.kole.platform.common.exception.KoleException;
import com.kole.platform.common.exception.ResourceNotFoundException;
import com.kole.platform.identity.application.dto.AuthResponse;
import com.kole.platform.identity.application.dto.LoginRequest;
import com.kole.platform.identity.application.dto.RegisterRequest;
import com.kole.platform.identity.application.dto.UserResponse;
import com.kole.platform.identity.domain.model.RefreshToken;
import com.kole.platform.identity.domain.model.Role;
import com.kole.platform.identity.domain.model.RoleName;
import com.kole.platform.identity.domain.model.User;
import com.kole.platform.identity.domain.model.UserStatus;
import com.kole.platform.identity.domain.repository.RefreshTokenRepository;
import com.kole.platform.identity.domain.repository.RoleRepository;
import com.kole.platform.identity.domain.repository.UserRepository;
import com.kole.platform.identity.infrastructure.security.JwtProperties;
import com.kole.platform.identity.infrastructure.security.JwtTokenService;
import com.kole.platform.identity.infrastructure.security.RefreshTokenGenerator;
import java.time.Instant;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final IdentityNormalizer normalizer;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenGenerator refreshTokenGenerator;
    private final JwtProperties jwtProperties;

    public AuthService(
        UserRepository userRepository,
        RoleRepository roleRepository,
        RefreshTokenRepository refreshTokenRepository,
        PasswordEncoder passwordEncoder,
        IdentityNormalizer normalizer,
        JwtTokenService jwtTokenService,
        RefreshTokenGenerator refreshTokenGenerator,
        JwtProperties jwtProperties
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository =
            refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.normalizer = normalizer;
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenGenerator =
            refreshTokenGenerator;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email =
            normalizer.normalizeEmail(request.email());

        String phoneNumber =
            normalizer.normalizePhoneNumber(
                request.phoneNumber()
            );

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException(
                "An account already exists with this email"
            );
        }

        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new ConflictException(
                "An account already exists with this phone number"
            );
        }

        Role householdRole = roleRepository
            .findByName(RoleName.HOUSEHOLD)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Default household role is unavailable"
                )
            );

        User user = new User(
            request.fullName().trim(),
            email,
            phoneNumber,
            passwordEncoder.encode(
                request.password()
            ),
            UserStatus.PENDING_VERIFICATION
        );

        user.addRole(householdRole);

        User savedUser = userRepository.save(user);

        return issueSession(
            savedUser,
            UUID.randomUUID()
        );
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email =
            normalizer.normalizeEmail(request.email());

        User user = userRepository
            .findByEmail(email)
            .orElseThrow(this::invalidCredentials);

        if (!passwordEncoder.matches(
            request.password(),
            user.getPasswordHash()
        )) {
            throw invalidCredentials();
        }

        if (!user.canAuthenticate()) {
            throw new KoleException(
                ErrorCode.ACCESS_DENIED,
                "This account is not permitted to authenticate"
            );
        }

        user.recordSuccessfulLogin(Instant.now());

        return issueSession(
            user,
            UUID.randomUUID()
        );
    }

    @Transactional
public AuthResponse refresh(String rawRefreshToken) {
    String tokenHash =
        refreshTokenGenerator.hash(rawRefreshToken);

    RefreshToken existingToken =
        refreshTokenRepository
            .findByTokenHash(tokenHash)
            .orElseThrow(this::invalidRefreshToken);

    Instant now = Instant.now();

    if (!existingToken.isActive(now)) {
        revokeTokenFamily(
            existingToken.getFamilyId(),
            now
        );

        throw invalidRefreshToken();
    }

    User user = existingToken.getUser();

    if (!user.canAuthenticate()) {
        revokeTokenFamily(
            existingToken.getFamilyId(),
            now
        );

        throw new KoleException(
            ErrorCode.ACCESS_DENIED,
            "This account is not permitted to authenticate"
        );
    }

    RefreshTokenGenerator.GeneratedRefreshToken generated =
        refreshTokenGenerator.generate();

    Instant refreshExpiresAt = now.plus(
        jwtProperties.refreshTokenTtl()
    );

    RefreshToken replacement = new RefreshToken(
        user,
        generated.hash(),
        existingToken.getFamilyId(),
        refreshExpiresAt
    );

    RefreshToken savedReplacement =
        refreshTokenRepository.saveAndFlush(
            replacement
        );

    existingToken.revoke(
        now,
        savedReplacement.getId()
    );

    JwtTokenService.GeneratedAccessToken accessToken =
        jwtTokenService.generate(user);

    return new AuthResponse(
        "Bearer",
        accessToken.value(),
        accessToken.expiresAt(),
        generated.rawToken(),
        refreshExpiresAt,
        UserResponse.from(user)
    );
}
    @Transactional
    public void logout(String rawRefreshToken) {
        String tokenHash =
            refreshTokenGenerator.hash(rawRefreshToken);

        refreshTokenRepository
            .findByTokenHash(tokenHash)
            .filter(token ->
                token.getRevokedAt() == null
            )
            .ifPresent(token ->
                token.revoke(
                    Instant.now(),
                    null
                )
            );
    }

    private AuthResponse issueSession(
        User user,
        UUID familyId
    ) {
        Instant now = Instant.now();

        JwtTokenService.GeneratedAccessToken accessToken =
            jwtTokenService.generate(user);

        RefreshTokenGenerator.GeneratedRefreshToken generated =
            refreshTokenGenerator.generate();

        Instant refreshExpiresAt = now.plus(
            jwtProperties.refreshTokenTtl()
        );

        RefreshToken refreshToken = new RefreshToken(
            user,
            generated.hash(),
            familyId,
            refreshExpiresAt
        );

        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(
            "Bearer",
            accessToken.value(),
            accessToken.expiresAt(),
            generated.rawToken(),
            refreshExpiresAt,
            UserResponse.from(user)
        );
    }

    private void revokeTokenFamily(
        UUID familyId,
        Instant revokedAt
    ) {
        refreshTokenRepository
            .findAllByFamilyId(familyId)
            .stream()
            .filter(token ->
                token.getRevokedAt() == null
            )
            .forEach(token ->
                token.revoke(
                    revokedAt,
                    null
                )
            );
    }

    private KoleException invalidCredentials() {
        return new KoleException(
            ErrorCode.AUTHENTICATION_REQUIRED,
            "Invalid email or password"
        );
    }

    private KoleException invalidRefreshToken() {
        return new KoleException(
            ErrorCode.AUTHENTICATION_REQUIRED,
            "Refresh token is invalid or expired"
        );
    }
}