package com.authservice.service;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConnectionChecker implements IBrokerConnectionChecker {

    private final ConnectionFactory connectionFactory;

    public RabbitMQConnectionChecker(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public boolean isBrokerReady() {
        try {
            connectionFactory.createConnection().close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
