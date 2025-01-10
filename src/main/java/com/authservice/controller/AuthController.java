package com.authservice.controller;

import com.authservice.dto.ErrorResponseDto;
import com.authservice.dto.JsonWrapper;
import com.authservice.dto.OperationResult;
import com.authservice.dto.RefreshTokenRequestDto;
import com.authservice.dto.UserDto;
import com.authservice.entity.UserEntity;
import com.authservice.enums.ErrorCode;
import com.authservice.repository.UserRepository;
import com.authservice.security.PasswordHasher;
import com.authservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(AuthService authService, UserRepository userRepository, PasswordHasher passwordHasher) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody UserDto userDto) throws Exception {
        UserEntity user = userRepository.findByEmail(userDto.getEmail());

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            ErrorResponseDto errorResponse = new ErrorResponseDto(
                    ErrorCode.INVALID_CREDENTIALS.name(),
                    "Invalid credentials"
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        boolean isPasswordValid = authService.checkPassword(userDto.getEmail(), userDto.getPassword());
        if (!isPasswordValid) {
            ErrorResponseDto errorResponse = new ErrorResponseDto(
                    ErrorCode.INVALID_CREDENTIALS.name(),
                    "Invalid credentials"
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        List<String> scopes = authService.getScopesForUser(userDto.getEmail());

        return authService.generateAndReturnTokens(user, scopes);
    }

    @PostMapping("/login/refresh")
    public ResponseEntity<Object> refreshAccessToken(@Valid @RequestBody RefreshTokenRequestDto request) throws
            Exception {
        String refreshToken = request.getRefreshToken();

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token is required");
        }

        UserEntity user = userRepository.findByRefreshToken(refreshToken);
        if (user == null) {
            ErrorResponseDto errorResponse = new ErrorResponseDto(
                    ErrorCode.INVALID_REFRESH_TOKEN.name(),
                    "Invalid or expired refresh token"
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        List<String> scopes = authService.getScopesForUser(user.getEmail());

        return authService.generateAndReturnTokens(user, scopes);
    }

    @PostMapping("/reset-password/initiate")
    public ResponseEntity<Object> initiatePasswordReset(@RequestParam String email) throws Exception {
        if (email == null || email.isBlank()) {
            ErrorResponseDto errorResponse = new ErrorResponseDto(
                    ErrorCode.INVALID_EMAIL.name(),
                    "Enter your password"
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
        UserEntity user = userRepository.findByEmail(email);

        if (user != null)
            authService.initiatePasswordReset(email);

        return ResponseEntity.ok("Password reset link has been sent to your email");
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token,
                                @RequestParam("password") String newPassword) throws Exception {
        authService.resetPassword(token, newPassword);
        return "redirect:/login";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create-user")
    public ResponseEntity<JsonWrapper<Object>> createUser(@RequestBody UserDto userDto) throws Exception {

        if (userDto.getPassword() == null || userDto.getPassword().isBlank()) {
            userDto.setPassword(null);
        }

        OperationResult<UserEntity> result = authService.createUser(userDto);

        if (result.isFailure()) {
            ErrorResponseDto errorResponse = new ErrorResponseDto(
                    ErrorCode.SERVER_ERROR.name(),
                    "Failed to create user"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new JsonWrapper<>(Collections.emptyList(), errorResponse));
        }
        return ResponseEntity.ok(new JsonWrapper<>(result.getValue()));
    }
}
