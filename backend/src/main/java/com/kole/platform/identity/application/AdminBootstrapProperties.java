package com.kole.platform.identity.application;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
    prefix = "kole.bootstrap.admin"
)
public record AdminBootstrapProperties(
    boolean enabled,
    String fullName,
    String email,
    String phoneNumber,
    String password
) {
}