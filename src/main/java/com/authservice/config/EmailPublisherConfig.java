package com.authservice.config;

import com.authservice.service.IEmailPublisher;
import com.authservice.service.RabbitMQEmailPublisher;
import com.authservice.service.RedisEmailPublisher;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailPublisherConfig {

    @Value("${email.publisher.type}")
    private String emailPublisherType;

    @Bean
    public IEmailPublisher emailPublisher(
            RabbitMQEmailPublisher rabbitMQEmailPublisher,
            RedisEmailPublisher redisEmailPublisher
    ) {
        return switch (emailPublisherType.toLowerCase()) {
            case "rabbitmq" -> rabbitMQEmailPublisher;
            case "redis" -> redisEmailPublisher;
            default -> throw new IllegalArgumentException("Unsupported email publisher type: " + emailPublisherType);
        };
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}