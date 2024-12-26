package com.authservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@ConfigurationProperties(prefix = "auth")
@Getter
@Setter
public class AuthConfig {

    private long refreshTokenLifetimeValue;
    private String issuer;
    private String privateKey;
    private String publicKey;
    private String resetPswrdLink;
    private String KID = UUID.randomUUID().toString();

}
