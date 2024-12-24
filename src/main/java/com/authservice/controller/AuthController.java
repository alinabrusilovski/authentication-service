package com.authservice.controller;

import com.authservice.dto.ErrorResponseDto;
import com.authservice.dto.RefreshTokenRequestDto;
import com.authservice.dto.UserDto;
import com.authservice.entity.UserEntity;
import com.authservice.enums.ErrorCode;
import com.authservice.repository.UserRepository;
import com.authservice.service.AuthService;
import jakarta.validation.Valid;
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
    public ResponseEntity<Object> login(@Valid @RequestBody UserDto userDto) throws Exception {

        boolean isPasswordValid = authService.checkPassword(userDto.getEmail(), userDto.getPassword());
        if (!isPasswordValid) {
            ErrorResponseDto errorResponse = new ErrorResponseDto(ErrorCode.INVALID_CREDENTIALS.name(), ErrorCode.INVALID_CREDENTIALS.getMessage());
            return ResponseEntity.status(401).body(errorResponse);
        }

        UserEntity user = userRepository.findByEmail(userDto.getEmail());
        List<String> scopes = authService.getScopesForUser(userDto.getEmail());

        return authService.generateAndReturnTokens(user, scopes);
    }

    @PostMapping("/login/refresh")
    public ResponseEntity<Object> refreshAccessToken(@Valid @RequestBody RefreshTokenRequestDto request) throws Exception {
        String refreshToken = request.getRefreshToken();

        UserEntity user = userRepository.findByRefreshToken(refreshToken);
        if (user == null) {
            ErrorResponseDto errorResponse = new ErrorResponseDto(ErrorCode.INVALID_REFRESH_TOKEN.name(), ErrorCode.INVALID_REFRESH_TOKEN.getMessage());
            return ResponseEntity.status(401).body(errorResponse);
        }

        List<String> scopes = authService.getScopesForUser(user.getEmail());

        ResponseEntity<Object> tokens = authService.generateAndReturnTokens(user, scopes);

        return ResponseEntity.ok(tokens);

    }

    @PostMapping("/reset-password/initiate")
    public ResponseEntity<Object> initiatePasswordReset(@RequestParam String email) {
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto(ErrorCode.INVALID_EMAIL.name(), ErrorCode.INVALID_EMAIL.getMessage()));
        }

        authService.initiatePasswordReset(email);

        return ResponseEntity.ok("Password reset link has been sent to your email");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Object> resetPassword(@RequestParam String token, @RequestParam String newPassword) throws Exception {
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto(ErrorCode.INVALID_TOKEN.name(), ErrorCode.INVALID_TOKEN.getMessage()));
        }

        if (newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto(ErrorCode.INVALID_PASSWORD.name(), ErrorCode.INVALID_PASSWORD.getMessage()));
        }

        authService.resetPassword(token, newPassword);

        return ResponseEntity.ok("Password has been reset successfully");
    }
}