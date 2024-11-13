package com.authservice.security;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

//@Component
public class PasswordHasher {
    private final SecureRandom random = new SecureRandom();
    private final SecretKeyFactory factory;

    public PasswordHasher() throws Exception {
        this.factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    }

    public String generateHash(String password) throws Exception {
        byte[] salt = new byte[16];
        random.nextBytes(salt); // Генерируем соль
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        byte[] hash = factory.generateSecret(spec).getEncoded(); // Получаем хеш

        byte[] saltAndHash = new byte[salt.length + hash.length];
        System.arraycopy(salt, 0, saltAndHash, 0, salt.length);
        System.arraycopy(hash, 0, saltAndHash, salt.length, hash.length);

        return Base64.getEncoder().encodeToString(saltAndHash); // Кодируем в Base64
    }

    public boolean checkHash(String encryptedPass, String openPass) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(encryptedPass); // Декодируем из Base64
        byte[] salt = Arrays.copyOfRange(decoded, 0, 16);
        byte[] hash = Arrays.copyOfRange(decoded, 16, decoded.length);

        PBEKeySpec spec = new PBEKeySpec(openPass.toCharArray(), salt, 65536, 128);
        byte[] calculatedHash = factory.generateSecret(spec).getEncoded();

        byte[] saltAndHash = new byte[salt.length + calculatedHash.length];
        System.arraycopy(salt, 0, saltAndHash, 0, salt.length);
        System.arraycopy(calculatedHash, 0, saltAndHash, salt.length, calculatedHash.length);

        return Arrays.equals(saltAndHash, decoded); // Сравниваем хеши
    }

}
