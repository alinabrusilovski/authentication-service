package com.authservice.controller;

import com.authservice.dto.ErrorResponseDto;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public ResponseEntity<Object> login(@RequestBody UserDto userDto) throws Exception {
        if (userDto.getEmail() == null || userDto.getPassword() == null) {
            ErrorResponseDto errorResponse = new ErrorResponseDto("VALIDATION_ERROR", "Email and password are required");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        boolean isPasswordValid = authService.checkPassword(userDto.getEmail(), userDto.getPassword());
        if (!isPasswordValid) {
            ErrorResponseDto errorResponse = new ErrorResponseDto("INVALID_CREDENTIALS", "Invalid credentials");
            return ResponseEntity.status(401).body(errorResponse);
        }

        UserEntity user = userRepository.findByEmail(userDto.getEmail());
        List<String> scopes = authService.getScopesForUser(userDto.getEmail());

        ResponseEntity<Object> tokens = authService.generateAndReturnTokens(user, scopes);

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/login/refresh")
    public ResponseEntity<Object> refreshAccessToken(@RequestBody RefreshTokenRequestDto request) throws Exception {
        String refreshToken = request.getRefreshToken();
        if (refreshToken == null || refreshToken.isBlank()) {
            ErrorResponseDto errorResponse = new ErrorResponseDto("VALIDATION_ERROR", "Refresh token is required");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        UserEntity user = userRepository.findByRefreshToken(refreshToken);
        if (user == null) {
            ErrorResponseDto errorResponse = new ErrorResponseDto("INVALID_REFRESH_TOKEN", "Invalid refresh token");
            return ResponseEntity.status(401).body(errorResponse);
        }

        List<String> scopes = authService.getScopesForUser(user.getEmail());

        ResponseEntity<Object> tokens = authService.generateAndReturnTokens(user, scopes);

        return ResponseEntity.ok(tokens);

    }

    @PostMapping("/reset-password/initiate")
    public ResponseEntity<Object> initiatePasswordReset(@RequestParam String email) {
        if (email == null || email.isBlank()) {
            String errorMsg = "Email cannot be null or empty";
            return ResponseEntity.badRequest().body(new ErrorResponseDto("INVALID_EMAIL", errorMsg));
        }

        authService.initiatePasswordReset(email);

        return ResponseEntity.ok("Password reset link has been sent to your email");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Object> resetPassword(@RequestParam String token, @RequestParam String newPassword) throws Exception {
        if (token == null || token.isBlank()) {
            String errorMsg = "Token cannot be null or empty";
            return ResponseEntity.badRequest().body(new ErrorResponseDto("INVALID_TOKEN", errorMsg));
        }

        if (newPassword == null || newPassword.isBlank()) {
            String errorMsg = "New password cannot be null or empty";
            return ResponseEntity.badRequest().body(new ErrorResponseDto("INVALID_PASSWORD", errorMsg));
        }

        authService.resetPassword(token, newPassword);

        return ResponseEntity.ok("Password has been reset successfully");
    }
}