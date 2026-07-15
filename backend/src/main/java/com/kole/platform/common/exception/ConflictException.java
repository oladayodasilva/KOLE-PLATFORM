package com.kole.platform.common.exception;

public class ConflictException extends KoleException {

    public ConflictException(String message) {
        super(ErrorCode.CONFLICT, message);
    }
}