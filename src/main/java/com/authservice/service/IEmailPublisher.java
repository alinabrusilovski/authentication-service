package com.authservice.service;

import com.authservice.dto.EmailMessage;
import org.springframework.stereotype.Component;

@Component
public interface IEmailPublisher {
    void sendEmailMessage(EmailMessage emailMessage);
}
