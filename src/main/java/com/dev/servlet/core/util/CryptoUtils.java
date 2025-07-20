package com.dev.servlet.core.util;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.dev.servlet.domain.transfer.dto.UserDTO;
import com.dev.servlet.domain.model.User;
import lombok.NoArgsConstructor;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class CryptoUtils {
    public static final int SEVEN_DAYS = 7 * 24 * 60 * 60 * 1000;
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

    private static byte[] getJwtSecretKey() throws Exception {
        String key = PropertiesUtil.getProperty("security.jwt.key");
        if (key == null) throw new Exception("Security key is not set");
        return key.getBytes();
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

    public static String generateJwtToken(UserDTO user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(getJwtSecretKey());
            long currentTimeMillis = System.currentTimeMillis();
            return JWT.create()
                    .withIssuer("Servlet")
                    .withSubject("Authentication")
                    .withClaim("userId", user.getId())
                    .withArrayClaim("roles", user.getPerfis().toArray(new Long[0]))
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(currentTimeMillis + SEVEN_DAYS))
                    .withJWTId(UUID.randomUUID().toString())
                    .sign(algorithm);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isValidToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        try {
            Algorithm algorithm = Algorithm.HMAC256(getJwtSecretKey());
            JWTVerifier verifier = JWT.require(algorithm).withIssuer("Servlet").build();
            DecodedJWT verified = verifier.verify(token);
            return verified.getExpiresAt() != null && !verified.getExpiresAt().before(new Date());
        } catch (Exception ignored) {
            return false;
        }
    }

    public static User getUser(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        Long userId = decodedJWT.getClaim("userId").asLong();
        List<Long> roles = decodedJWT.getClaim("roles").asList(Long.class);
        User user = new User(userId);
        user.setPerfis(roles);
        user.setToken(token);
        return user;
    }
}
