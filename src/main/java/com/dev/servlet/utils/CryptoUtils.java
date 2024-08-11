package com.dev.servlet.utils;

import com.dev.servlet.domain.User;
import com.dev.servlet.dto.UserDto;
import com.dev.servlet.mapper.UserMapper;
import org.apache.commons.lang3.RandomStringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Base64;

public final class CryptoUtils {

    public static final String BLOWFISH = "Blowfish";

    private CryptoUtils() {
    }

    private static byte[] getSecurityKey() throws Exception {
        String key = PropertiesUtil.getProperty("security.encrypt.key");
        if (key == null) throw new Exception("Security key is not set");
        return key.getBytes();
    }

    private static String getSecurityAlgorithm() throws Exception {
        String key = PropertiesUtil.getProperty("security.encrypt.algorithm");
        if (key == null) throw new Exception("Cypher algorithm is not set");
        return key;
    }

    /**
     * Decrypt the String
     *
     * @param text
     * @return
     */
    public static String decrypt(String text) {
        try {
            String cryptherAlgorithm = getSecurityAlgorithm();
            byte[] securityKey = getSecurityKey();
            SecretKeySpec key = new SecretKeySpec(securityKey, cryptherAlgorithm);
            Cipher cipher = Cipher.getInstance(BLOWFISH);
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] encrypted = Base64.getDecoder().decode(text);
            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Encrypt the String
     *
     * @param text
     * @return
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

    /**
     * Get a new token
     *
     * @param user
     * @return
     */
    public static String generateToken(UserDto user) {
        String token = Instant.now().toEpochMilli() + RandomStringUtils.randomAlphanumeric(6);
        CacheUtil.storeToken(token, user);
        return token;
    }

    /**
     * Verify if the token really exists
     *
     * @param token
     * @return
     */
    public static boolean isValidToken(String token) {
        if (token == null) {
            return false;
        }
        return CacheUtil.hasToken(token);
    }

    public static boolean validate(String planText, String cipherText) {
        String encrypt = encrypt(planText);
        return encrypt.equals(cipherText);
    }
}
