package com.authservice.service;

import com.authservice.dto.EmailMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEmailPublisherService implements IEmailPublisherService {

    private final KafkaTemplate<String, EmailMessage> kafkaTemplate;
    private final String topicName;


    public KafkaEmailPublisherService(KafkaTemplate<String, EmailMessage> kafkaTemplate, @Value("${email.kafka.topic}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    @Override
    public void sendEmailMessage(EmailMessage emailMessage) {
        kafkaTemplate.send(topicName, emailMessage);
    }
}