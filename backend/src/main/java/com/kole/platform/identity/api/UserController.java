package com.kole.platform.identity.api;

import com.kole.platform.common.api.ApiResponse;
import com.kole.platform.common.web.RequestContext;
import com.kole.platform.identity.application.CurrentUserService;
import com.kole.platform.identity.application.dto.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final CurrentUserService currentUserService;

    public UserController(
        CurrentUserService currentUserService
    ) {
        this.currentUserService =
            currentUserService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(
        @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(
            ApiResponse.success(
                "Current user retrieved successfully",
                currentUserService.getCurrentUser(jwt),
                RequestContext.requestId()
            )
        );
    }
}