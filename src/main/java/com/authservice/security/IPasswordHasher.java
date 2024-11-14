package com.authservice.security;

import org.springframework.stereotype.Component;

@Component
public interface IPasswordHasher {
    String generateHash(String password) throws Exception;
    boolean checkHash(String encryptedPass, String openPass) throws Exception;
}
