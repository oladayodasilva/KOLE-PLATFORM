package com.kole.platform.common.exception;

public class KoleException extends RuntimeException {

    private final ErrorCode errorCode;

    public KoleException(
        ErrorCode errorCode,
        String message
    ) {
        super(message);
        this.errorCode = errorCode;
    }

    public KoleException(
        ErrorCode errorCode,
        String message,
        Throwable cause
    ) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}