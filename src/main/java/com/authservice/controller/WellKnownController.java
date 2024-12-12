package com.authservice.controller;

import com.authservice.config.AuthConfig;
import com.authservice.exception.KeyGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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

@RestController
public class WellKnownController {

    private static final Logger logger = LoggerFactory.getLogger(WellKnownController.class);

    private final AuthConfig authConfig;

    @Autowired
    public WellKnownController(AuthConfig authConfig) {
        this.authConfig = authConfig;
    }

    @GetMapping("/.well-known/jwks.json")
    public ResponseEntity<Map<String, Object>> getJwks() throws JsonProcessingException {

        String publicKeyJson = authConfig.getPublicKey();

        if (publicKeyJson == null || publicKeyJson.isEmpty()) {
            String errorMsg = "Public key is missing or empty";
            logger.error(errorMsg);
            throw new KeyGenerationException(errorMsg);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> publicKeyMap = objectMapper.readValue(publicKeyJson, new TypeReference<Map<String, String>>() {
        });

        String n = publicKeyMap.get("n");
        String e = publicKeyMap.get("e");

        if (n == null || e == null) {
            String errorMsg = "Modulus (n) or Exponent (e) are missing from the public key";
            logger.error(errorMsg);
            throw new KeyGenerationException(errorMsg);
        }

        Map<String, Object> jwk = generateJwk(n, e);
        logger.info("Successfully generated JWK");

        return new ResponseEntity<>(Map.of("keys", List.of(jwk)), HttpStatus.OK);
    }

    private Map<String, Object> generateJwk(String n, String e) {
        logger.info("Generating JWK from modulus (n) and exponent (e)");

        return Map.of(
                "kty", "RSA",
                "e", e,
                "use", "sig",
                "kid", authConfig.getKID(),
                "alg", "RS256",
                "n", n
        );
    }

    @GetMapping("/.well-known/openid-configuration")
    public ResponseEntity<Map<String, Object>> getOpenIdConfiguration() {
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
    }
}