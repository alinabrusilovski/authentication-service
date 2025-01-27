package com.authservice.controller;

import com.authservice.service.IHealthCheckable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/is-ready")
public class ConnectionCheckController {

    private final IHealthCheckable emailPublisher;

    @Autowired
    public ConnectionCheckController(IHealthCheckable emailPublisher) {
        this.emailPublisher = emailPublisher;
    }

    @GetMapping
    public ResponseEntity<String> checkBrokerConnection() {
        boolean isReady = emailPublisher.isReady();
        String name = emailPublisher.getName();

        String response = isReady
                ? name + " is ready"
                : name + " is not ready";
        return ResponseEntity.ok(response);
    }
}