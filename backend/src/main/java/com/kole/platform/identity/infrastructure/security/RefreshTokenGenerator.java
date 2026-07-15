package com.kole.platform.identity.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenGenerator {

    private static final int TOKEN_BYTES = 64;

    private final SecureRandom secureRandom =
        new SecureRandom();

    public GeneratedRefreshToken generate() {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);

        String rawToken = Base64
            .getUrlEncoder()
            .withoutPadding()
            .encodeToString(bytes);

        return new GeneratedRefreshToken(
            rawToken,
            hash(rawToken)
        );
    }

    public String hash(String rawToken) {
        try {
            MessageDigest digest =
                MessageDigest.getInstance("SHA-256");

            byte[] hashed = digest.digest(
                rawToken.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormatSupport.toHex(hashed);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                "SHA-256 is unavailable",
                exception
            );
        }
    }

    public record GeneratedRefreshToken(
        String rawToken,
        String hash
    ) {
    }

    private static final class HexFormatSupport {

        private static final char[] HEX =
            "0123456789abcdef".toCharArray();

        private HexFormatSupport() {
        }

        static String toHex(byte[] bytes) {
            char[] output = new char[bytes.length * 2];

            for (int index = 0;
                 index < bytes.length;
                 index++) {

                int value = bytes[index] & 0xff;

                output[index * 2] =
                    HEX[value >>> 4];

                output[index * 2 + 1] =
                    HEX[value & 0x0f];
            }

            return new String(output);
        }
    }
}