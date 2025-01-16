package com.authservice.service;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class KafkaConnectionChecker implements IBrokerConnectionChecker {

    private final KafkaAdmin kafkaAdmin;

    public KafkaConnectionChecker(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
    }

    @Override
    public boolean isBrokerReady() {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            adminClient.listTopics(new ListTopicsOptions().timeoutMs(1000)).names().get(1, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}