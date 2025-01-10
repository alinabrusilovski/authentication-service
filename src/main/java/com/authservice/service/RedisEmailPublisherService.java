package com.authservice.service;

import com.authservice.dto.EmailMessage;
import org.springframework.stereotype.Component;

@Component
public class RedisEmailPublisherService implements IEmailPublisherService{
    @Override
    public void sendEmailMessage(EmailMessage emailMessage) {

    }
}
