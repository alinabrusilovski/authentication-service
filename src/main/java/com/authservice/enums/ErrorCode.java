package com.authservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    VALIDATION_ERROR("Validation error"),
    INVALID_CREDENTIALS("Invalid credentials"),
    INVALID_REFRESH_TOKEN("Invalid refresh token"),
    INVALID_EMAIL("Invalid email"),
    INVALID_PASSWORD("Invalid password"),
    INVALID_TOKEN("Invalid token"),
    KEY_MISSING("Public key is missing"),
    KEY_PROCESSING_ERROR("Error processing public key"),
    KEY_FORMAT_ERROR("Invalid key format"),
    SERVER_ERROR("Unexpected server error"),
    FORBIDDEN_ACTION("Forbidden action: You are not allowed to perform this action");


    private final String message;

}
