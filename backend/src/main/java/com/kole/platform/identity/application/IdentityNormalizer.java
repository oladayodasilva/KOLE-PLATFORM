package com.kole.platform.identity.application;

import com.kole.platform.common.exception.BusinessRuleException;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class IdentityNormalizer {

    public String normalizeEmail(String email) {
        return email
            .trim()
            .toLowerCase(Locale.ROOT);
    }

    public String normalizePhoneNumber(String phoneNumber) {
        String normalized = phoneNumber
            .trim()
            .replaceAll("[\\s()-]", "");

        if (!normalized.startsWith("+")) {
            throw new BusinessRuleException(
                "Phone number must use international format, "
                    + "for example +2348012345678"
            );
        }

        if (!normalized.matches("^\\+[1-9]\\d{7,14}$")) {
            throw new BusinessRuleException(
                "Phone number is not in a valid international format"
            );
        }

        return normalized;
    }
}