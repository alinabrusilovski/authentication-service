package com.authservice.service;

import com.authservice.dto.EmailMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Primary
public class RabbitMQEmailPublisher implements IEmailPublisher, IHealthCheckable {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    private final ConnectionFactory connectionFactory;

    public RabbitMQEmailPublisher(RabbitTemplate rabbitTemplate,
                                  @Value("${email.rabbitmq.exchange}") String exchange,
                                  @Value("${email.rabbitmq.routing-key}") String routingKey,
                                  ConnectionFactory connectionFactory) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void sendEmailMessage(EmailMessage emailMessage) {
        rabbitTemplate.convertAndSend(exchange, routingKey, emailMessage);
        log.debug("Sending email message to exchange: {}, routingKey: {}, message: {}",
                exchange, routingKey, emailMessage);
    }

    @Override
    public String getName() {
        return "RabbitMQ";
    }

    @Override
    public boolean isReady() {
        try {
            connectionFactory.createConnection().close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

