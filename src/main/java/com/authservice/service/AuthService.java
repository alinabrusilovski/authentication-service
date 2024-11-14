package com.authservice.service;

import com.authservice.dto.UserDto;
import com.authservice.repository.UserRepository;
import com.authservice.security.IPasswordHasher;
import com.authservice.security.PasswordHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final IPasswordHasher passwordHasher;

    @Autowired
    public AuthService(
            UserRepository userRepository,
            PasswordHasher passwordHasher
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;

    }

    @Override
    public boolean checkPassword(String email, String password) throws Exception {
        UserDto user = userRepository.findByEmail(email);

        if (user == null) {
            return false;
        }
        String storedHash = user.getPassword();

        return passwordHasher.checkHash(storedHash, password);
    }
}


