package com.authservice.controller;

import com.authservice.service.IBrokerConnectionChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/is-broker-ready")
public class BrokerConnectionCheckController {

    private final IBrokerConnectionChecker brokerConnectionChecker;

    @Value("${email.publisher.type}")
    private String brokerName;

    @Autowired
    public BrokerConnectionCheckController(IBrokerConnectionChecker brokerConnectionChecker) {
        this.brokerConnectionChecker = brokerConnectionChecker;
    }

    @GetMapping
    public ResponseEntity<String> checkBrokerConnection() {
        boolean isReady = brokerConnectionChecker.isBrokerReady();

        String response = isReady
                ? brokerName + " is ready"
                : brokerName + " is not ready";
        return ResponseEntity.ok(response);
    }
}
