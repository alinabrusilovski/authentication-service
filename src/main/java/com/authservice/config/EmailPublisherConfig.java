package com.authservice.config;

import com.authservice.service.CompositeEmailPublisherService;
import com.authservice.service.IEmailPublisherService;
import com.authservice.service.KafkaEmailPublisherService;
import com.authservice.service.RabbitMQEmailPublisherService;
import com.authservice.service.RedisEmailPublisherService;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


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
        if (emailPublisherType.equalsIgnoreCase("all")) {
            return new CompositeEmailPublisherService(
                    List.of(kafkaEmailPublisherService, rabbitMQEmailPublisherService, redisEmailPublisherService)
            );
        }
        return switch (emailPublisherType.toLowerCase()) {
            case "rabbitmq" -> rabbitMQEmailPublisherService;
            case "kafka" -> kafkaEmailPublisherService;
            case "redis" -> redisEmailPublisherService;
            default -> throw new IllegalArgumentException("Unsupported email publisher type: " + emailPublisherType);
        };
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }



}