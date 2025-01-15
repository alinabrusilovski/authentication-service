package com.authservice.service;

import com.authservice.dto.EmailMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.RedisTemplate;

@Component
@Slf4j
public class RedisEmailPublisherService implements IEmailPublisherService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final String redisChannel;

    @Autowired
    public RedisEmailPublisherService(RedisTemplate<String, Object> redisTemplate,
                                      @Value("${email.redis.channel}") String redisChannel) {
        this.redisTemplate = redisTemplate;
        this.redisChannel = redisChannel;
    }

    @Override
    public void sendEmailMessage(EmailMessage emailMessage) {
        try {
            redisTemplate.convertAndSend(redisChannel, emailMessage);
            log.info("Email message sent to Redis channel: {}", redisChannel);
        } catch (Exception e) {
            log.error("Failed to send email message to Redis channel: {}", redisChannel, e);
        }
    }
}
