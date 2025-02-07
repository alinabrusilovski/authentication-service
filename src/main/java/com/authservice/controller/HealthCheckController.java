package com.authservice.controller;

import com.authservice.dto.HealthStatusDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/health")
@Tag(name = "Health Check Controller", description = "Controller for checking the health status of the application")
public class HealthCheckController {

    @GetMapping
    @Operation(summary = "Health Check", description = "Checks if the application is running and returns its health status")
    @ApiResponse(responseCode = "200", description = "The application is running and the health status is returned successfully")
    public ResponseEntity<HealthStatusDto> healthCheck() {
        HealthStatusDto status = new HealthStatusDto("Application is running");
        return ResponseEntity.ok(status);
    }
}
