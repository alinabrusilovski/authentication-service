package com.authservice.dto;

public record EmailMessage(String email,
                           String subject,
                           String body,
                           String publisherType) {
}