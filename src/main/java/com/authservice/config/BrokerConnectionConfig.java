package com.authservice.config;

import com.authservice.service.IBrokerConnectionChecker;
import com.authservice.service.KafkaConnectionChecker;
import com.authservice.service.RabbitMQConnectionChecker;
import com.authservice.service.RedisConnectionChecker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BrokerConnectionConfig {

    @Value("${email.publisher.type}")
    private String broker;

    @Bean
    public IBrokerConnectionChecker brokerConnectionChecker(
            KafkaConnectionChecker kafkaConnectionChecker,
            RabbitMQConnectionChecker rabbitMQConnectionChecker,
            RedisConnectionChecker redisConnectionChecker
    ) {
        return switch (broker.toLowerCase()) {
            case "rabbitmq" -> rabbitMQConnectionChecker;
            case "kafka" -> kafkaConnectionChecker;
            case "redis" -> redisConnectionChecker;
            default -> throw new IllegalArgumentException("Unsupported broker type: " + broker);
        };
    }
}
