package com.authservice.service;

import com.authservice.dto.EmailMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CompositeEmailPublisherService implements IEmailPublisherService {

    private final List<IEmailPublisherService> publishers;

    public CompositeEmailPublisherService(List<IEmailPublisherService> publishers) {
        this.publishers = publishers;
    }

    @Override
    public void sendEmailMessage(EmailMessage emailMessage) {
        for (IEmailPublisherService publisher : publishers) {
            publisher.sendEmailMessage(emailMessage);
        }
    }
}