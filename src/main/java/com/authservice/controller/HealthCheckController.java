package com.authservice.controller;

import com.authservice.dto.HealthStatusDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthCheckController {

    @GetMapping
    public ResponseEntity<HealthStatusDto> healthCheck() {
        HealthStatusDto status = new HealthStatusDto("Application is running");
        return ResponseEntity.ok(status);
    }
}
