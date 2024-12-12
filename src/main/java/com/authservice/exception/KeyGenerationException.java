package com.authservice.exception;

public class KeyGenerationException extends RuntimeException {
    public KeyGenerationException(String message) {
        super(message);
    }

    public KeyGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}