package com.authservice.service;

import com.authservice.dto.EmailMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Primary
public class RabbitMQEmailPublisherService implements IEmailPublisherService {

    private final RabbitTemplate rabbitTemplate;
    private final String queue;

    public RabbitMQEmailPublisherService(RabbitTemplate rabbitTemplate,
                                         @Value("${email.rabbitmq.queue}") String queue) {
        this.rabbitTemplate = rabbitTemplate;
        this.queue = queue;
    }

    @Override
    public void sendEmailMessage(EmailMessage emailMessage) {
        rabbitTemplate.convertAndSend(queue, emailMessage);
        log.debug("Sending password reset request for email: {}", emailMessage);
    }
}
