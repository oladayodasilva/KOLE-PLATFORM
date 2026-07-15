package com.kole.platform.identity.infrastructure.security;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kole.security.jwt")
public record JwtProperties(
    String issuer,
    String secret,
    Duration accessTokenTtl,
    Duration refreshTokenTtl
) {
}