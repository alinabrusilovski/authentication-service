package com.authservice.controller;

import com.authservice.dto.RefreshTokenRequestDto;
import com.authservice.dto.UserDto;
import com.authservice.entity.UserEntity;
import com.authservice.repository.UserRepository;
import com.authservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserDto userDto) throws Exception {
        if (userDto.getEmail() == null || userDto.getPassword() == null) {
            throw new IllegalArgumentException("Email and password are required");
        }

        boolean isPasswordValid = authService.checkPassword(userDto.getEmail(), userDto.getPassword());
        if (!isPasswordValid) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        UserEntity user = userRepository.findByEmail(userDto.getEmail());
        List<String> scopes = authService.getScopesForUser(userDto.getEmail());

        Map<String, Object> tokens = authService.generateAndReturnTokens(user, scopes);

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/login/refresh")
    public ResponseEntity<Map<String, Object>> refreshAccessToken(@RequestBody RefreshTokenRequestDto request) throws Exception {
        String refreshToken = request.getRefreshToken();
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token is required");
        }

        UserEntity user = userRepository.findByRefreshToken(refreshToken);
        if (user == null) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        List<String> scopes = authService.getScopesForUser(user.getEmail());

        Map<String, Object> tokens = authService.generateAndReturnTokens(user, scopes);

        return ResponseEntity.ok(tokens);

    }
}