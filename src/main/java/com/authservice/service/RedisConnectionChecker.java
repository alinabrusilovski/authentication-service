package com.authservice.service;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

@Component
public class RedisConnectionChecker implements IBrokerConnectionChecker {

    private final RedisConnectionFactory connectionFactory;

    public RedisConnectionChecker(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public boolean isBrokerReady() {
        try {
            return connectionFactory.getConnection().ping() != null;
        } catch (Exception e) {
            return false;
        }
    }
}
