package com.servletstack.infrastructure.utils;

import com.servletstack.infrastructure.config.Properties;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PasswordHasher {

    private static final SecureRandom RANDOM = new SecureRandom();

    // Salt is used to prevent rainbow table attacks
    private static final int SALT_LENGTH;
    // Hash length determines the size of the resulting hash
    private static final int HASH_LENGTH;
    /// Argon2 parameters
    // These can be adjusted based on security and performance requirements
    private static final int ITERATIONS;
    // Memory cost in kilobytes
    private static final int MEMORY_KB;
    // Degree of parallelism
    private static final int PARALLELISM = 1;

    static {
        HASH_LENGTH = Properties.getOrDefault("security.password.hash_length", 32);
        SALT_LENGTH = Properties.getOrDefault("security.password.salt_length", 16);
        ITERATIONS = Math.max(1, Properties.getOrDefault("security.password.iterations", 4));
        MEMORY_KB = Properties.getOrDefault("security.password.memory_kb", 8192); // 8 MB
    }

    public static String hash(String password) {
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        byte[] hash = derive(password.toCharArray(), salt);
        return encode(salt, hash);
    }

    /**
     * Verify a password against a stored hash
     *
     * @param password - plaintext password
     * @param stored   - stored hash
     * @return true if the password matches the stored hash, false otherwise
     */
    public static boolean verify(String password, String stored) {
        Decoded decoded = decode(stored);
        byte[] computed = derive(password.toCharArray(), decoded.salt);
        return constantTimeEquals(decoded.hash, computed);
    }

    /**
     * Derive a hash from the password and salt using Argon2
     *
     * @param password - plaintext password
     * @param salt     - salt bytes
     * @return derived hash bytes
     */
    private static byte[] derive(char[] password, byte[] salt) {
        Argon2Parameters params = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withSalt(salt)
                .withIterations(ITERATIONS)
                .withMemoryAsKB(MEMORY_KB)
                .withParallelism(PARALLELISM)
                .build();

        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(params);

        byte[] hash = new byte[HASH_LENGTH];
        generator.generateBytes(password, hash);
        return hash;
    }

    private static String encode(byte[] salt, byte[] hash) {
        return Base64.getEncoder().encodeToString(salt) + ":" +
               Base64.getEncoder().encodeToString(hash);
    }

    private static Decoded decode(String value) {
        String[] parts = value.split(":");
        if (parts.length != 2) throw new IllegalArgumentException("Invalid hash");

        return new Decoded(
                Base64.getDecoder().decode(parts[0]),
                Base64.getDecoder().decode(parts[1])
        );
    }

    /**
     * Constant-time comparison to prevent timing attacks
     *
     * @param a - first byte array
     * @param b - second byte array
     * @return true if both arrays are equal, false otherwise
     */
    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }

    private record Decoded(byte[] salt, byte[] hash) {
    }
}
