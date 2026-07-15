package com.kole.platform.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    VALIDATION_ERROR(
        "VALIDATION_ERROR",
        HttpStatus.BAD_REQUEST
    ),

    MALFORMED_REQUEST(
        "MALFORMED_REQUEST",
        HttpStatus.BAD_REQUEST
    ),

    RESOURCE_NOT_FOUND(
        "RESOURCE_NOT_FOUND",
        HttpStatus.NOT_FOUND
    ),

    CONFLICT(
        "CONFLICT",
        HttpStatus.CONFLICT
    ),

    ACCESS_DENIED(
        "ACCESS_DENIED",
        HttpStatus.FORBIDDEN
    ),

    AUTHENTICATION_REQUIRED(
        "AUTHENTICATION_REQUIRED",
        HttpStatus.UNAUTHORIZED
    ),

    BUSINESS_RULE_VIOLATION(
        "BUSINESS_RULE_VIOLATION",
        HttpStatus.UNPROCESSABLE_ENTITY
    ),

    INTERNAL_ERROR(
        "INTERNAL_ERROR",
        HttpStatus.INTERNAL_SERVER_ERROR
    );

    private final String code;
    private final HttpStatus status;

    ErrorCode(String code, HttpStatus status) {
        this.code = code;
        this.status = status;
    }

    public String code() {
        return code;
    }

    public HttpStatus status() {
        return status;
    }
}