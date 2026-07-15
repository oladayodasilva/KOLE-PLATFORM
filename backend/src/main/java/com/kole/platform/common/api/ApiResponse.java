package com.kole.platform.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    boolean success,
    String message,
    T data,
    ApiError error,
    Instant timestamp,
    String requestId
) {

    public static <T> ApiResponse<T> success(
        String message,
        T data,
        String requestId
    ) {
        return new ApiResponse<>(
            true,
            message,
            data,
            null,
            Instant.now(),
            requestId
        );
    }

    public static ApiResponse<Void> success(
        String message,
        String requestId
    ) {
        return new ApiResponse<>(
            true,
            message,
            null,
            null,
            Instant.now(),
            requestId
        );
    }

    public static ApiResponse<Void> failure(
        String message,
        ApiError error,
        String requestId
    ) {
        return new ApiResponse<>(
            false,
            message,
            null,
            error,
            Instant.now(),
            requestId
        );
    }
}