package com.kole.platform.identity.application.dto;

import com.kole.platform.identity.domain.model.RoleName;
import com.kole.platform.identity.domain.model.User;
import com.kole.platform.identity.domain.model.UserStatus;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record UserResponse(
    UUID id,
    String fullName,
    String email,
    String phoneNumber,
    UserStatus status,
    boolean emailVerified,
    boolean phoneVerified,
    Set<RoleName> roles,
    Instant createdAt
) {

    public static UserResponse from(User user) {
        Set<RoleName> roleNames = user
            .getRoles()
            .stream()
            .map(role -> role.getName())
            .collect(Collectors.toUnmodifiableSet());

        return new UserResponse(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getPhoneNumber(),
            user.getStatus(),
            user.isEmailVerified(),
            user.isPhoneVerified(),
            roleNames,
            user.getCreatedAt()
        );
    }
}