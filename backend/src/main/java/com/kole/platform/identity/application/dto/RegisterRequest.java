package com.kole.platform.identity.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

    @NotBlank(message = "Full name is required")
    @Size(
        min = 2,
        max = 150,
        message = "Full name must contain between 2 and 150 characters"
    )
    String fullName,

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(
        max = 254,
        message = "Email must not exceed 254 characters"
    )
    String email,

    @NotBlank(message = "Phone number is required")
    @Size(
        max = 30,
        message = "Phone number must not exceed 30 characters"
    )
    String phoneNumber,

    @NotBlank(message = "Password is required")
    @Size(
        min = 12,
        max = 128,
        message = "Password must contain between 12 and 128 characters"
    )
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
        message =
            "Password must include uppercase, lowercase and a number"
    )
    String password
) {
}