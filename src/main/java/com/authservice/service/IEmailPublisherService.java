package com.authservice.service;

import com.authservice.dto.EmailMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public interface IEmailPublisherService {
    void sendEmailMessage(EmailMessage emailMessage);
}
