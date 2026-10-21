package com.shopsphere.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Enterprise-grade password hashing utility using PBKDF2 with HMAC-SHA256.
 * Zero external dependencies, standard Java crypto.
 */
public class PasswordUtil {

    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_BYTES = 16;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    private PasswordUtil() {
    }

    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_BYTES];
            random.nextBytes(salt);

            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();

            // Format: iterations:base64(salt):base64(hash)
            return ITERATIONS + ":" + 
                   Base64.getEncoder().encodeToString(salt) + ":" + 
                   Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error occurred while hashing password", e);
        }
    }

    public static boolean verifyPassword(String password, String storedHash) {
        if (password == null || storedHash == null) {
            return false;
        }
        try {
            String[] parts = storedHash.split(":");
            if (parts.length != 3) {
                return false;
            }

            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[2]);

            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, expectedHash.length * 8);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] testHash = factory.generateSecret(spec).getEncoded();

            // Constant-time comparison to prevent timing attacks
            int diff = expectedHash.length ^ testHash.length;
            for (int i = 0; i < expectedHash.length && i < testHash.length; i++) {
                diff |= expectedHash[i] ^ testHash[i];
            }
            return diff == 0;
        } catch (Exception e) {
            return false;
        }
    }
}

