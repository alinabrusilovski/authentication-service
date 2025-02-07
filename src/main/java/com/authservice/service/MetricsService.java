package com.authservice.service;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    // Technical Metrics
    static final Counter authRequests = Counter.build()
            .name("auth_requests_total")
            .help("Total number of authentication requests.")
            .register();

    static final Counter authErrors = Counter.build()
            .name("auth_errors_total")
            .help("Total number of authentication errors.")
            .register();

    static final Gauge passwordResetRequests = Gauge.build()
            .name("password_reset_requests_total")
            .help("Total number of password reset requests.")
            .register();

    // Business Metrics
    static final Counter successfulAuths = Counter.build()
            .name("successful_auths_total")
            .help("Total number of successful authentications.")
            .register();

    static final Counter failedAuths = Counter.build()
            .name("failed_auths_total")
            .help("Total number of failed authentications.")
            .register();

    static final Counter passwordResets = Counter.build()
            .name("password_resets_total")
            .help("Total number of password reset requests.")
            .register();

    public void increaseAuthRequestCount() {
        authRequests.inc();
    }

    public void increaseAuthErrorCount() {
        authErrors.inc();
    }

    public void increasePasswordResetCount() {
        passwordResetRequests.inc();
    }

    public void increaseSuccessfulAuth() {
        successfulAuths.inc();
    }

    public void increaseFailedAuth() {
        failedAuths.inc();
    }

    public void increasePasswordResetRequest() {
        passwordResets.inc();
    }
}
