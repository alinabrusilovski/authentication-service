package com.authservice.controller;

import com.authservice.service.IHealthCheckable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/is-ready")
@Tag(name = "Connection Check Controller", description = "Controller to check the readiness of external connections (e.g., message broker)")
public class ConnectionCheckController {

    private final IHealthCheckable emailPublisher;

    @Autowired
    public ConnectionCheckController(IHealthCheckable emailPublisher) {
        this.emailPublisher = emailPublisher;
    }

    @GetMapping
    @Operation(summary = "Check Broker Connection", description = "Checks whether the message broker (or external dependency) is ready")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connection readiness status returned successfully")
    })
    public ResponseEntity<String> checkBrokerConnection() {
        boolean isReady = emailPublisher.isReady();
        String name = emailPublisher.getName();

        String response = isReady
                ? name + " is ready"
                : name + " is not ready";
        return ResponseEntity.ok(response);
    }
}