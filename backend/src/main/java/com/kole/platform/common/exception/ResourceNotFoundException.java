package com.kole.platform.common.exception;

public class ResourceNotFoundException extends KoleException {

    public ResourceNotFoundException(String message) {
        super(ErrorCode.RESOURCE_NOT_FOUND, message);
    }
}