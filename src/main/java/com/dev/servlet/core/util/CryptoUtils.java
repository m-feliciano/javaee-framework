package com.dev.servlet.core.util;

import lombok.NoArgsConstructor;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class CryptoUtils {

    private static byte[] getSecurityKey() throws Exception {
        String key = System.getenv("SECURITY_ENCRYPT_KEY");
        if (key == null) throw new Exception("Security key is not set");
        return key.getBytes();
    }

    private static String getSecurityAlgorithm() throws Exception {
        String key = Properties.get("security.encrypt.algorithm");
        if (key == null) throw new Exception("Cypher algorithm is not set");
        return key;
    }

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
