package com.authservice.service;

import org.springframework.stereotype.Component;

@Component
public interface IBrokerConnectionChecker {
    boolean isBrokerReady();
}
