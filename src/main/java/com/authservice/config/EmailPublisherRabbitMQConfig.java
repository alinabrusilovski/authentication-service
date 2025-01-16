package com.authservice.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailPublisherRabbitMQConfig {

    @Bean
    public DirectExchange emailExchange(@Value("${email.rabbitmq.exchange}") String exchangeName) {
        return new DirectExchange(exchangeName);
    }
}
