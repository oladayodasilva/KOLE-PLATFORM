package com.kole.platform.identity.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

@Configuration
public class IdentitySecurityBeans {

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories
            .createDelegatingPasswordEncoder();
    }

    @Bean
    SecretKey jwtSecretKey(JwtProperties properties) {
        byte[] decoded;

        try {
            decoded = Base64
                .getDecoder()
                .decode(properties.secret());
        } catch (IllegalArgumentException exception) {
            decoded = properties
                .secret()
                .getBytes(StandardCharsets.UTF_8);
        }

        if (decoded.length < 32) {
            throw new IllegalStateException(
                "JWT_SECRET must contain at least 256 bits"
            );
        }

        return new SecretKeySpec(
            decoded,
            "HmacSHA256"
        );
    }

    @Bean
    JwtEncoder jwtEncoder(SecretKey secretKey) {
        return new NimbusJwtEncoder(
            new ImmutableSecret<>(secretKey)
        );
    }

    @Bean
    JwtDecoder jwtDecoder(SecretKey secretKey) {
        return NimbusJwtDecoder
            .withSecretKey(secretKey)
            .build();
    }
}