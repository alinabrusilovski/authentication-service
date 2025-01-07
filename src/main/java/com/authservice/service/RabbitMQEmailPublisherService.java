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
    private final String exchange;
    private final String routingKey;

    public RabbitMQEmailPublisherService(RabbitTemplate rabbitTemplate,
                                         @Value("${email.rabbitmq.exchange}") String exchange,
                                         @Value("${email.rabbitmq.routing-key}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    @Override
    public void sendEmailMessage(EmailMessage emailMessage) {
        rabbitTemplate.convertAndSend(exchange, routingKey, emailMessage);
        log.debug("Sending password reset request for email: {}", emailMessage);

    }
}
