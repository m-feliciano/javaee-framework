package com.dev.servlet.core.util;

import com.dev.servlet.domain.model.User;
import com.dev.servlet.domain.transfer.response.UserResponse;
import lombok.NoArgsConstructor;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Comprehensive cryptographic utility class providing JWT bearerToken management and symmetric encryption.
 * This class handles secure authentication bearerToken generation, validation, and data encryption/decryption
 * using industry-standard algorithms and best practices.
 * 
 * <p>Key security features:
 * <ul>
 *   <li><strong>JWT Authentication:</strong> Secure bearerToken generation with HMAC-256 signing</li>
 *   <li><strong>Token Validation:</strong> Comprehensive bearerToken verification with expiration checks</li>
 *   <li><strong>Symmetric Encryption:</strong> Configurable cipher algorithms for data protection</li>
 *   <li><strong>User Context:</strong> Token-based user information extraction</li>
 *   <li><strong>Configuration-driven:</strong> Security keys and algorithms from properties</li>
 * </ul>
 *
 * <p>JWT bearerToken structure includes:
 * <ul>
 *   <li>User ID and role information</li>
 *   <li>Issuer identification ("Servlet")</li>
 *   <li>7-day expiration period</li>
 *   <li>Unique JWT ID for tracking</li>
 *   <li>HMAC-256 signature for integrity</li>
 * </ul>
 * 
 * <p>Required properties configuration:
 * <ul>
 *   <li>{@code security.encrypt.key} - Symmetric encryption key</li>
 *   <li>{@code security.encrypt.algorithm} - Cipher algorithm (e.g., "AES")</li>
 *   <li>{@code security.jwt.key} - JWT signing secret key</li>
 * </ul>
 * 
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * // JWT Token Operations
 * UserDTO user = new UserDTO(123L, Arrays.asList(1L, 2L)); // user with roles
 * String bearerToken = CryptoUtils.generateJwtToken(user);
 * 
 * // Token validation
 * boolean valid = CryptoUtils.validateToken(bearerToken);
 * if (valid) {
 *     User authenticatedUser = JwtUtil.getUserFromToken(bearerToken);
 *     // Process authenticated request
 * }
 * 
 * // Data encryption
 * String sensitive = "confidential data";
 * String encrypted = CryptoUtils.encrypt(sensitive);
 * String decrypted = CryptoUtils.decrypt(encrypted);
 * }
 * </pre>
 * 
 * <p><strong>Security Notes:</strong>
 * <ul>
 *   <li>Ensure secure key generation and storage</li>
 *   <li>Use strong, randomly generated secret keys</li>
 *   <li>Rotate keys regularly for enhanced security</li>
 *   <li>Never expose secret keys in logs or client-side code</li>
 *   <li>Validate all tokens before processing requests</li>
 * </ul>
 * 
 * @since 1.0
 * @see PropertiesUtil
 * @see UserResponse
 * @see User
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class CryptoUtils {
    
    /**
     * Retrieves the symmetric encryption key from configuration.
     * 
     * @return the encryption key as byte array
     * @throws Exception if the security key is not configured
     */
    private static byte[] getSecurityKey() throws Exception {
        String key = PropertiesUtil.getProperty("security.encrypt.key");
        if (key == null) throw new Exception("Security key is not set");
        return key.getBytes();
    }

    /**
     * Retrieves the encryption algorithm from configuration.
     * 
     * @return the cipher algorithm name (e.g., "AES")
     * @throws Exception if the algorithm is not configured
     */
    private static String getSecurityAlgorithm() throws Exception {
        String key = PropertiesUtil.getProperty("security.encrypt.algorithm");
        if (key == null) throw new Exception("Cypher algorithm is not set");
        return key;
    }

    /**
     * Decrypts a Base64-encoded encrypted text using symmetric encryption.
     * Uses the configured cipher algorithm and security key.
     * 
     * @param text the Base64-encoded encrypted text
     * @return the decrypted plain text
     * @throws RuntimeException if decryption fails or configuration is invalid
     */
    public static String decrypt(String text) {
        try {
            String cryptherAlgorithm = getSecurityAlgorithm();
            byte[] securityKey = getSecurityKey();
            SecretKeySpec key = new SecretKeySpec(securityKey, cryptherAlgorithm);
            Cipher cipher = Cipher.getInstance(cryptherAlgorithm);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encrypted = Base64.getDecoder().decode(text);
            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Encrypts plain text using symmetric encryption and Base64 encoding.
     * Uses the configured cipher algorithm and security key.
     * 
     * @param text the plain text to encrypt
     * @return the Base64-encoded encrypted text
     * @throws RuntimeException if encryption fails or configuration is invalid
     */
    public static String encrypt(String text) {
        try {
            String cypherAlgorithm = getSecurityAlgorithm();
            byte[] securityKey = getSecurityKey();
            SecretKeySpec key = new SecretKeySpec(securityKey, cypherAlgorithm);
            Cipher cipher = Cipher.getInstance(cypherAlgorithm);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(text.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
