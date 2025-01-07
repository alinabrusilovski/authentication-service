package com.authservice.config;

import com.authservice.service.IEmailPublisherService;
import com.authservice.service.KafkaEmailPublisherService;
import com.authservice.service.RabbitMQEmailPublisherService;
import com.authservice.service.RedisEmailPublisherService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class EmailPublisherConfig {

    @Value("${email.publisher.type}")
    private String emailPublisherType;

    @Bean
    public IEmailPublisherService emailPublisherService(
            KafkaEmailPublisherService kafkaEmailPublisherService,
            RabbitMQEmailPublisherService rabbitMQEmailPublisherService,
            RedisEmailPublisherService redisEmailPublisherService
    ) {
        return switch (emailPublisherType.toLowerCase()) {
            case "rabbitmq" -> rabbitMQEmailPublisherService;
            case "kafka" -> kafkaEmailPublisherService;
            case "redis" -> redisEmailPublisherService;
            default -> throw new IllegalArgumentException("Unsupported email publisher type: " + emailPublisherType);
        };
    }
}