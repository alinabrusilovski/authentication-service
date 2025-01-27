package com.authservice.service;

import com.authservice.dto.EmailMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component
@Slf4j
public class RedisEmailPublisher implements IEmailPublisher, IHealthCheckable {

    private final RedisConnectionFactory connectionFactory;

    @Value("${email.redis.host}")
    private String redisHost;
    @Value("${email.redis.port}")
    private int redisPort;
    @Value("${email.redis.channel}")
    private String redisChannel;

    private final JedisPool jedisPool;

    public RedisEmailPublisher(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        this.jedisPool = new JedisPool(poolConfig, redisHost, redisPort);
    }

    public void sendEmailMessage(EmailMessage emailMessage) {
        try (Jedis jedis = jedisPool.getResource()) {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonMessage = objectMapper.writeValueAsString(emailMessage);

            jedis.publish(redisChannel, jsonMessage);
            log.info("Email message sent to Redis channel: {}", redisChannel);
        } catch (Exception e) {
            log.error("Failed to send email message to Redis channel: {}", redisChannel, e);
        }
    }

    @Override
    public String getName() {
        return "Redis";
    }

    @Override
    public boolean isReady() {
        try {
            return connectionFactory.getConnection().ping() != null;
        } catch (Exception e) {
            return false;
        }
    }
}