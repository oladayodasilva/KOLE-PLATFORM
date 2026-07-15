package com.kole.platform.identity.application;

import com.kole.platform.identity.domain.model.Role;
import com.kole.platform.identity.domain.model.RoleName;
import com.kole.platform.identity.domain.model.User;
import com.kole.platform.identity.domain.model.UserStatus;
import com.kole.platform.identity.domain.repository.RoleRepository;
import com.kole.platform.identity.domain.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminBootstrapInitializer
    implements ApplicationRunner {

    private static final Logger log =
        LoggerFactory.getLogger(
            AdminBootstrapInitializer.class
        );

    private final AdminBootstrapProperties properties;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final IdentityNormalizer normalizer;

    public AdminBootstrapInitializer(
        AdminBootstrapProperties properties,
        UserRepository userRepository,
        RoleRepository roleRepository,
        PasswordEncoder passwordEncoder,
        IdentityNormalizer normalizer
    ) {
        this.properties = properties;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.normalizer = normalizer;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments arguments) {
        log.info(
            "Admin bootstrap enabled={}",
            properties.enabled()
        );

        if (!properties.enabled()) {
            log.info("Admin bootstrap is disabled");
            return;
        }

        validateProperties();

        String email =
            normalizer.normalizeEmail(
                properties.email()
            );

        String phoneNumber =
            normalizer.normalizePhoneNumber(
                properties.phoneNumber()
            );

        if (userRepository.existsByEmail(email)) {
            log.info(
                "Bootstrap admin already exists email={}",
                email
            );
            return;
        }

        if (userRepository.existsByPhoneNumber(
            phoneNumber
        )) {
            log.warn(
                "Bootstrap admin phone number already exists"
            );
            return;
        }

        Role adminRole = roleRepository
            .findByName(RoleName.ADMIN)
            .orElseThrow(() ->
                new IllegalStateException(
                    "ADMIN role is missing"
                )
            );

        User admin = new User(
            properties.fullName().trim(),
            email,
            phoneNumber,
            passwordEncoder.encode(
                properties.password()
            ),
            UserStatus.ACTIVE
        );

        admin.addRole(adminRole);

        userRepository.saveAndFlush(admin);

        log.info(
            "Bootstrap admin created email={}",
            email
        );
    }

    private void validateProperties() {
        if (isBlank(properties.fullName())
            || isBlank(properties.email())
            || isBlank(properties.phoneNumber())
            || isBlank(properties.password())) {

            throw new IllegalStateException(
                "Admin bootstrap is enabled but required values are missing"
            );
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}