package com.kole.platform.identity.infrastructure.security;

import com.kole.platform.identity.domain.model.User;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties properties;

    public JwtTokenService(
        JwtEncoder jwtEncoder,
        JwtProperties properties
    ) {
        this.jwtEncoder = jwtEncoder;
        this.properties = properties;
    }

    public GeneratedAccessToken generate(User user) {
        Objects.requireNonNull(
            user,
            "User must not be null"
        );

        if (user.getId() == null) {
            throw new IllegalArgumentException(
                "User ID must not be null"
            );
        }

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(
            properties.accessTokenTtl()
        );

        List<String> roles = user.getRoles()
            .stream()
            .map(role -> String.valueOf(role.getName()))
            .toList();

        JwtClaimsSet claims = JwtClaimsSet
            .builder()
            .issuer(properties.issuer())
            .issuedAt(issuedAt)
            .expiresAt(expiresAt)
            .subject(user.getId().toString())
            .claim("email", user.getEmail())
            .claim("name", user.getFullName())
            .claim("roles", roles)
            .build();

        JwsHeader header = JwsHeader
            .with(MacAlgorithm.HS256)
            .build();

        String token = jwtEncoder
            .encode(
                JwtEncoderParameters.from(
                    header,
                    claims
                )
            )
            .getTokenValue();

        return new GeneratedAccessToken(
            token,
            issuedAt,
            expiresAt
        );
    }

    public record GeneratedAccessToken(
        String value,
        Instant issuedAt,
        Instant expiresAt
    ) {
    }
}