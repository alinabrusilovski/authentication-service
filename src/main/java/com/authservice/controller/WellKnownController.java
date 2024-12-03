package com.authservice.controller;

import com.authservice.config.AuthConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class WellKnownController {

    private static final Logger logger = LoggerFactory.getLogger(WellKnownController.class);

    private static final String KID = UUID.randomUUID().toString();

    private final AuthConfig authConfig;

    @Autowired
    public WellKnownController(AuthConfig authConfig) {
        this.authConfig = authConfig;
    }

    @GetMapping("/.well-known/jwks.json")
    public ResponseEntity<Map<String, Object>> getJwks() {
        try {
            String publicKeyJson = authConfig.getPublicKey();

            if (publicKeyJson == null || publicKeyJson.isEmpty()) {
                logger.error("Public key is missing or empty");
                return ResponseEntity.status(500).build();
            }

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> publicKeyMap = objectMapper.readValue(publicKeyJson, Map.class);

            String n = publicKeyMap.get("n");
            String e = publicKeyMap.get("e");

            if (n == null || e == null) {
                logger.error("Modulus (n) or Exponent (e) are missing from the public key");
                return ResponseEntity.status(500).build();
            }

            Map<String, Object> jwk = generateJwk(n, e);
            logger.info("Successfully generated JWK");

            return new ResponseEntity<>(Map.of("keys", List.of(jwk)), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while processing public key to generate JWK", e);
            return ResponseEntity.status(500).build();
        }
    }

    private Map<String, Object> generateJwk(String n, String e) {
        try {
            logger.info("Generating JWK from modulus (n) and exponent (e)");

            return Map.of(
                    "kty", "RSA",
                    "e", e,
                    "use", "sig",
                    "kid", KID,
                    "alg", "RS256",
                    "n", n
            );
        } catch (Exception exception) {
            logger.error("Failed to generate JWK", exception);
            return Map.of("error", "Failed to generate JWK");
        }
    }

    @GetMapping("/.well-known/openid-configuration")
    public ResponseEntity<Map<String, Object>> getOpenIdConfiguration() {
        try {
            String host = authConfig.getIssuer();
            Map<String, Object> configuration = Map.of(
                    "jwks_uri", host + "/.well-known/jwks.json",
                    "subject_types_supported", List.of("public"),
                    "id_token_signing_alg_values_supported", List.of("RS256"),
                    "issuer", host,
                    "code_challenge_methods_supported", List.of("plain", "S256")
            );
            logger.info("Successfully fetched OpenID configuration");
            return new ResponseEntity<>(configuration, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while fetching OpenID configuration", e);
            return ResponseEntity.status(500).build();
        }
    }
}