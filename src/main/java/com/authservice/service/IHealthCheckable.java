package com.authservice.service;

import org.springframework.stereotype.Component;

@Component
public interface IHealthCheckable {
    String getName();
    boolean isReady();
}
