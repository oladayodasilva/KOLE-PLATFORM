package com.kole.platform.common.exception;

public class BusinessRuleException extends KoleException {

    public BusinessRuleException(String message) {
        super(ErrorCode.BUSINESS_RULE_VIOLATION, message);
    }
}