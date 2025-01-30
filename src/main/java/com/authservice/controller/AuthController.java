package com.authservice.controller;

import com.authservice.config.CaptchaVerification;
import com.authservice.dto.ErrorResponseDto;
import com.authservice.dto.JsonWrapper;
import com.authservice.dto.OperationResult;
import com.authservice.dto.RefreshTokenRequestDto;
import com.authservice.dto.UserDto;
import com.authservice.entity.UserEntity;
import com.authservice.enums.ErrorCode;
import com.authservice.repository.UserRepository;
import com.authservice.service.CaptchaService;
import com.authservice.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication Controller", description = "Controller for managing authentication and authorization")
public class AuthController {

    private final IAuthService authService;
    private final UserRepository userRepository;
    private final CaptchaVerification captchaVerification;

    @Autowired
    public AuthController(IAuthService authService, UserRepository userRepository, CaptchaVerification captchaVerification) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.captchaVerification = captchaVerification;
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Allows a user to log in using email and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful authentication"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })

    public ResponseEntity<Object> login(@RequestBody @Valid @Schema(description = "User login credentials") UserDto userDto) throws Exception {
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
    @Operation(summary = "Refresh Access Token", description = "Refreshes the access token using a valid refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access token successfully refreshed"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })

    public ResponseEntity<Object> refreshAccessToken(
            @RequestBody @Valid @Schema(description = "Request containing the refresh token") RefreshTokenRequestDto request) throws Exception {
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
    @Operation(summary = "Initiate Password Reset", description = "Sends a password reset link to the user's email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset link sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid email address or captcha verification failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<Object> initiatePasswordReset(
            @RequestParam @Parameter(description = "User's email address") String email,
            @RequestParam("g-recaptcha-response") String captchaResponse) throws Exception {

        if (email == null || email.isBlank()) {
            ErrorResponseDto errorResponse = new ErrorResponseDto(
                    ErrorCode.INVALID_EMAIL.name(),
                    "Enter your password"
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }

        boolean isCaptchaValid = captchaVerification.verifyCaptcha(captchaResponse);
        if (!isCaptchaValid) {
            ErrorResponseDto errorResponse = new ErrorResponseDto(
                    ErrorCode.INVALID_CAPTCHA.name(),
                    "Captcha verification failed"
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }

        UserEntity user = userRepository.findByEmail(email);

        if (user != null)
            authService.initiatePasswordReset(email, captchaResponse);

        return ResponseEntity.ok("Password reset link has been sent to your email");
    }

    @GetMapping("/reset-password")
    @Operation(summary = "Password Reset Form", description = "Displays the password reset form for a user")
    public String resetPasswordForm(
            @RequestParam("token") @Parameter(description = "Password reset token") String token, Model model) {

        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset Password", description = "Resets the user's password using the provided token and new password")
    public String resetPassword(@RequestParam("token") @Parameter(description = "Password reset token") String token,
                                @RequestParam("password") @Parameter(description = "New password for the user") String newPassword) throws Exception {

        authService.resetPassword(token, newPassword);
        return "redirect:/login";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create-user")
    @Operation(summary = "Create New User", description = "Allows an admin to create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully"),
            @ApiResponse(responseCode = "500", description = "Server error occurred",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<JsonWrapper<Object>> createUser(
            @RequestBody @Schema(description = "Details of the new user") UserDto userDto) throws Exception {

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
