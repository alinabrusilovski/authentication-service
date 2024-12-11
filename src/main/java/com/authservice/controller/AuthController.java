package com.authservice.controller;

import com.authservice.config.AuthConfig;
import com.authservice.dto.UserDto;
import com.authservice.entity.UserEntity;
import com.authservice.repository.UserRepository;
import com.authservice.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final AuthConfig authConfig;

    @Autowired
    public AuthController(AuthService authService, ObjectMapper objectMapper, UserRepository userRepository, AuthConfig authConfig) {
        this.authService = authService;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this. authConfig = authConfig;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDto userDto) throws JsonProcessingException {
        try {
            authService.checkPassword(userDto.getEmail(), userDto.getPassword());

            UserEntity user = userRepository.findByEmail(userDto.getEmail());
            List<String> scopes = authService.getScopesForUser(userDto.getEmail());

            String accessToken = authService.generateJwtToken(user, scopes);
            String refreshToken = authService.generateRefreshToken();
            LocalDateTime refreshTokenExpiry = authService.calculateTokenExpiry(authConfig.getRefreshTokenLifetimeValue(), authConfig.getRefreshTokenLifetimeUnit());

            authService.updateRefreshTokenForUser(user, refreshToken, refreshTokenExpiry);

            Map<String, Object> responseMap = Map.of(
                    "access_token", accessToken,
                    "refresh_token", refreshToken,
                    "refresh_token_expired", refreshTokenExpiry.toString()
            );
            String responseJson = objectMapper.writeValueAsString(responseMap);

            return ResponseEntity.ok(responseJson);

        } catch (Exception e) {
            Map<String, Object> responseMap = Map.of("message", "An error occurred: " + e.getMessage());
            String responseJson = objectMapper.writeValueAsString(responseMap);
            return ResponseEntity.internalServerError().body(responseJson);
        }
    }

    @PostMapping("/login/refresh")
    public ResponseEntity<Map<String, Object>> refreshAccessToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refresh_token");
            if (refreshToken == null || refreshToken.isBlank()) {
                return new ResponseEntity<>(Map.of("message", "Refresh token is required"), HttpStatus.BAD_REQUEST);
            }

            UserEntity user = userRepository.findByRefreshToken(refreshToken);

            List<String> scopes = authService.getScopesForUser(user.getEmail());
            String newAccessToken = authService.generateJwtToken(user, scopes);
            String newRefreshToken = authService.generateRefreshToken();
            LocalDateTime newRefreshTokenExpiry = authService.calculateTokenExpiry(authConfig.getRefreshTokenLifetimeValue(), authConfig.getRefreshTokenLifetimeUnit());

            authService.updateRefreshTokenForUser(user, newRefreshToken, newRefreshTokenExpiry);

            Map<String, Object> response = Map.of(
                    "access_token", newAccessToken,
                    "refresh_token", newRefreshToken,
                    "refresh_token_expired", newRefreshTokenExpiry.toString()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "An error occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}