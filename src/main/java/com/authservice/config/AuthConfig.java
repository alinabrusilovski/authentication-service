package com.authservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.temporal.ChronoUnit;

@Configuration
@ConfigurationProperties(prefix = "auth")
@Getter
@Setter
public class AuthConfig {

    private long refreshTokenLifetimeValue;
    private String refreshTokenLifetimeUnit;
    private String issuer;
    private String privateKey;
    private String publicKey;

    public long getLifetimeInSeconds() {
        ChronoUnit chronoUnit = ChronoUnit.valueOf(refreshTokenLifetimeUnit.toUpperCase());
        return chronoUnit.getDuration().getSeconds() * refreshTokenLifetimeValue;
    }

}
