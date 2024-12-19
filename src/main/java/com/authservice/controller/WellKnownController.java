package com.authservice.controller;

import com.authservice.config.AuthConfig;
import com.authservice.dto.ErrorResponseDto;
import com.authservice.dto.JwkKeyDto;
import com.authservice.dto.JwkResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class WellKnownController {

    private final AuthConfig authConfig;

    @Autowired
    public WellKnownController(AuthConfig authConfig) {
        this.authConfig = authConfig;
    }

    @GetMapping("/.well-known/jwks.json")
    public ResponseEntity<Object> getJwks() throws JsonProcessingException {

        String publicKeyJson = authConfig.getPublicKey();

        if (publicKeyJson == null || publicKeyJson.isEmpty()) {
            String errorMsg = "Public key is missing or empty";
            log.error(errorMsg);
            ErrorResponseDto errorResponse = new ErrorResponseDto("KEY_MISSING", errorMsg);
            return ResponseEntity.status(400).body(errorResponse);
        }

        Map<String, String> publicKeyMap = parsePublicKeyJson(publicKeyJson);
        if (publicKeyMap == null) {
            String errorMsg = "Error processing public key";
            log.error(errorMsg);
            ErrorResponseDto errorResponse = new ErrorResponseDto("KEY_PROCESSING_ERROR", errorMsg);
            return ResponseEntity.status(500).body(errorResponse);
        }

        String n = publicKeyMap.get("n");
        String e = publicKeyMap.get("e");

        if (n == null || e == null) {
            String errorMsg = "Modulus (n) or Exponent (e) are missing from the public key";
            log.error(errorMsg);
            ErrorResponseDto errorResponse = new ErrorResponseDto("KEY_FORMAT_ERROR", errorMsg);
            return ResponseEntity.status(400).body(errorResponse);
        }

        ResponseEntity<JwkResponseDto> jwk = generateJwk(n, e);
        log.info("Successfully generated JWK");

        return ResponseEntity.ok(jwk);
    }

    private Map<String, String> parsePublicKeyJson(String publicKeyJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(publicKeyJson, new TypeReference<>() {
        });
    }

    private ResponseEntity<JwkResponseDto> generateJwk(String n, String e) {
        log.info("Generating JWK from modulus (n) and exponent (e)");

        JwkKeyDto singleKey = new JwkKeyDto(
                "RSA",
                e,
                "sig",
                authConfig.getKID(),
                "RS256",
                n
        );

        JwkResponseDto jwkResponse = new JwkResponseDto(List.of(singleKey));

        return ResponseEntity.ok(jwkResponse);
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
        log.info("Successfully fetched OpenID configuration");
        return new ResponseEntity<>(configuration, HttpStatus.OK);
    }
}
