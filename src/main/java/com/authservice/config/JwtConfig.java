package com.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
public class JwtConfig {

    @Bean
    public JwtDecoder jwtDecoder(SecurityProperties securityProperties) {
        String jwksUri = securityProperties.getJwksUri();
        return NimbusJwtDecoder.withJwkSetUri(jwksUri).build();
    }
}
