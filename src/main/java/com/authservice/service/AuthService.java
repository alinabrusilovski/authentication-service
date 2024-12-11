package com.authservice.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.authservice.config.AuthConfig;
import com.authservice.entity.ScopeEntity;
import com.authservice.entity.UserEntity;
import com.authservice.repository.UserRepository;
import com.authservice.security.IPasswordHasher;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final IPasswordHasher passwordHasher;
    private final AuthConfig authConfig;

    @Autowired
    public AuthService(
            UserRepository userRepository,
            IPasswordHasher passwordHasher,
            AuthConfig authConfig
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.authConfig = authConfig;
    }

    @Override
    public boolean checkPassword(String email, String password) throws Exception {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            return false;
        }
        String storedHash = user.getPassword();
        return passwordHasher.checkHash(storedHash, password);
    }

    public List<String> getScopesForUser(String email) {
        UserEntity user = userRepository.findByEmail(email);
        if (user != null && user.getScopes() != null) {
            return user.getScopes().stream()
                    .map(ScopeEntity::getName)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public String generateJwtToken(UserEntity user, List<String> scopes) throws Exception {
        String issuer = authConfig.getIssuer();
        String privateKeyString = authConfig.getPrivateKey();
        PrivateKey privateKey = loadPrivateKeyFromConfig(privateKeyString);

        return JWT.create()
                .withIssuer(issuer)
                .withClaim("id", user.getId().toString())
                .withClaim("scope", scopes)
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600000))
                .sign(Algorithm.RSA256(null, (RSAPrivateKey) privateKey));
    }

    public String generateRefreshToken() {
        byte[] randomBytes = new byte[200];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getEncoder().encodeToString(randomBytes);
    }

    public LocalDateTime calculateTokenExpiry(long lifetimeValue, String lifetimeUnit) {
        ChronoUnit chronoUnit;
        try {
            chronoUnit = ChronoUnit.valueOf(lifetimeUnit.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid lifetime unit: " + lifetimeUnit, e);
        }

        return LocalDateTime.now().plus(lifetimeValue, chronoUnit);
    }

    private PrivateKey loadPrivateKeyFromConfig(String privateKeyString) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
    }

    @Transactional
    public void updateRefreshTokenForUser(UserEntity user, String refreshToken, LocalDateTime expiryTime) {
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpired(expiryTime);
        userRepository.save(user);
    }

}