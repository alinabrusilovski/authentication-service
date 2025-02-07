package com.authservice.dto;

public record ResetPasswordRequestDto(String email, String captchaResponse) {
}
