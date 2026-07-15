package com.kole.platform.identity.api;

import com.kole.platform.common.api.ApiResponse;
import com.kole.platform.common.web.RequestContext;
import com.kole.platform.identity.application.AuthService;
import com.kole.platform.identity.application.dto.AuthResponse;
import com.kole.platform.identity.application.dto.LoginRequest;
import com.kole.platform.identity.application.dto.LogoutRequest;
import com.kole.platform.identity.application.dto.RefreshTokenRequest;
import com.kole.platform.identity.application.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(
        AuthService authService
    ) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
        @Valid @RequestBody RegisterRequest request
    ) {
        AuthResponse response =
            authService.register(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                ApiResponse.success(
                    "Account created successfully",
                    response,
                    RequestContext.requestId()
                )
            );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
        @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(
            ApiResponse.success(
                "Login successful",
                authService.login(request),
                RequestContext.requestId()
            )
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
        @Valid @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(
            ApiResponse.success(
                "Session refreshed successfully",
                authService.refresh(
                    request.refreshToken()
                ),
                RequestContext.requestId()
            )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
        @Valid @RequestBody LogoutRequest request
    ) {
        authService.logout(
            request.refreshToken()
        );

        return ResponseEntity.ok(
            ApiResponse.success(
                "Logout successful",
                RequestContext.requestId()
            )
        );
    }
}