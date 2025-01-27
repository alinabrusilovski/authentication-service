package com.authservice.config;

import com.authservice.dto.EmailMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class RedisEmailPublisherConfig {

    @Value("${email.redis.channel}")
    private String redisChannel;
    @Value("${email.redis.host}")
    private String host;
    @Value("${email.redis.port}")
    private Integer port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public RedisTemplate<String, EmailMessage> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, EmailMessage> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.deactivateDefaultTyping();
        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        return template;
//        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(new ObjectMapper());
//        template.setKeySerializer(serializer);
//        template.setValueSerializer(serializer);
//        template.setHashKeySerializer(serializer);
//        template.setHashValueSerializer(serializer);
//
//        return template;
    }

    @Bean
    public ChannelTopic redisChannelTopic() {
        return new ChannelTopic(redisChannel);
    }
}
