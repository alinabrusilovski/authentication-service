package com.authservice.config;

import com.authservice.dto.EmailMessage;
import com.authservice.service.CompositeEmailPublisherService;
import com.authservice.service.IEmailPublisherService;
import com.authservice.service.KafkaEmailPublisherService;
import com.authservice.service.RabbitMQEmailPublisherService;
import com.authservice.service.RedisEmailPublisherService;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Configuration
public class EmailPublisherConfig {

    @Value("${email.publisher.type}")
    private String emailPublisherType;

    @Value("${email.kafka.bootstrap-servers}")
    private String kafkaBootstrapServers;

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
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public KafkaTemplate<String, EmailMessage> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<String, EmailMessage> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

}