package com.kole.platform.identity.application;

import com.kole.platform.common.exception.ResourceNotFoundException;
import com.kole.platform.identity.application.dto.UserResponse;
import com.kole.platform.identity.domain.repository.UserRepository;
import java.util.UUID;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(
        UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(Jwt jwt) {
        UUID userId;

        try {
            userId = UUID.fromString(
                jwt.getSubject()
            );
        } catch (IllegalArgumentException exception) {
            throw new ResourceNotFoundException(
                "Authenticated user could not be resolved"
            );
        }

        return userRepository
            .findById(userId)
            .map(UserResponse::from)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Authenticated user was not found"
                )
            );
    }
}