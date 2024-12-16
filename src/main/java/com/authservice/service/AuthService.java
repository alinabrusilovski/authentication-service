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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class AuthService implements IAuthService {

    private static final ChronoUnit LIFETIME_UNIT = ChronoUnit.HOURS;

    private final UserRepository userRepository;
    private final IPasswordHasher passwordHasher;
    private final AuthConfig authConfig;
    private final SecureRandom secureRandom;

    @Autowired
    public AuthService(
            UserRepository userRepository,
            IPasswordHasher passwordHasher,
            AuthConfig authConfig,
            SecureRandom secureRandom
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.authConfig = authConfig;
        this.secureRandom = secureRandom;
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
        secureRandom.nextBytes(randomBytes);
        return Base64.getEncoder().encodeToString(randomBytes);
    }

    private PrivateKey loadPrivateKeyFromConfig(String privateKeyString) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
    }

    @Transactional
    public void updateRefreshTokenForUser(UserEntity user, String refreshToken, OffsetDateTime expiryTime) {
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpired(expiryTime);
        userRepository.save(user);
    }

    public Map<String, Object> generateAndReturnTokens(UserEntity user, List<String> scopes) throws Exception {
        String accessToken = generateJwtToken(user, scopes);
        String refreshToken = generateRefreshToken();
        OffsetDateTime refreshTokenExpiry = calculateTokenExpiry(authConfig.getRefreshTokenLifetimeValue());

        updateRefreshTokenForUser(user, refreshToken, refreshTokenExpiry);

        return Map.of(
                "access_token", accessToken,
                "refresh_token", refreshToken,
                "refresh_token_expired", refreshTokenExpiry.toString()
        );
    }

    public OffsetDateTime calculateTokenExpiry(long lifetimeValue) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        return now.plus(lifetimeValue, LIFETIME_UNIT);
    }

}