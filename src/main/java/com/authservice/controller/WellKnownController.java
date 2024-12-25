package com.authservice.controller;

import com.authservice.config.AuthConfig;
import com.authservice.dto.ErrorResponseDto;
import com.authservice.dto.JwkKeyDto;
import com.authservice.dto.JwkResponseDto;
import com.authservice.enums.ErrorCode;
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
            log.error(ErrorCode.KEY_MISSING.getMessage());
            ErrorResponseDto errorResponse = new ErrorResponseDto(
                    ErrorCode.KEY_MISSING.name(),
                    ErrorCode.KEY_MISSING.getMessage()
            );
            return ResponseEntity.status(400).body(errorResponse);
        }

        Map<String, String> publicKeyMap = parsePublicKeyJson(publicKeyJson);
        if (publicKeyMap == null) {
            log.error(ErrorCode.KEY_PROCESSING_ERROR.getMessage());
            ErrorResponseDto errorResponse = new ErrorResponseDto(
                    ErrorCode.KEY_PROCESSING_ERROR.name(),
                    ErrorCode.KEY_PROCESSING_ERROR.getMessage()
            );
            return ResponseEntity.status(500).body(errorResponse);
        }

        String n = publicKeyMap.get("n");
        String e = publicKeyMap.get("e");

        if (n == null || e == null) {
            log.error(ErrorCode.KEY_FORMAT_ERROR.getMessage());
            ErrorResponseDto errorResponse = new ErrorResponseDto(
                    ErrorCode.KEY_FORMAT_ERROR.name(),
                    ErrorCode.KEY_FORMAT_ERROR.getMessage()
            );
            return ResponseEntity.status(400).body(errorResponse);
        }

        JwkResponseDto jwk = generateJwk(n, e);
        log.info("Successfully generated JWK");

        return ResponseEntity.ok(jwk);
    }

    private Map<String, String> parsePublicKeyJson(String publicKeyJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(publicKeyJson, new TypeReference<>() {
        });
    }

    private JwkResponseDto generateJwk(String n, String e) {
        log.info("Generating JWK from modulus (n) and exponent (e)");

        JwkKeyDto singleKey = new JwkKeyDto(
                "RSA",
                e,
                "sig",
                authConfig.getKID(),
                "RS256",
                n
        );
        return new JwkResponseDto(List.of(singleKey));
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
