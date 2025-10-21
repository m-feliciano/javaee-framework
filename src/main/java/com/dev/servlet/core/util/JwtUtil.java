package com.dev.servlet.core.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.dev.servlet.domain.model.User;

import javax.inject.Singleton;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Singleton
public class JwtUtil {
    private static final long ACCESS_TOKEN_MS = 7L * 24 * 60 * 60 * 1000; // 7 days
    private static final long REFRESH_TOKEN_MS = 30L * 24 * 60 * 60 * 1000; // 30 days
    private static final String ISSUER = "Servlet";
    private static final String SUBJECT = "Authentication";
    private static final String BEARER_PREFIX = "Bearer ";
    private final Algorithm algorithm;

    public JwtUtil() {
        algorithm = Algorithm.HMAC256(getJwtSecretBytes());
    }

    private byte[] getJwtSecretBytes() {
        String key = PropertiesUtil.getProperty("security.jwt.key");
        if (key == null || key.isBlank())
            throw new IllegalStateException("security.jwt.key is not configured");

        return key.getBytes(StandardCharsets.UTF_8);
    }

    private String extractToken(String bearerToken) {
        if (bearerToken == null) return null;

        String t = bearerToken.trim();
        if (t.startsWith(BEARER_PREFIX))
            return t.substring(BEARER_PREFIX.length()).trim();

        return t;
    }

    public String generateAccessToken(User user) {
        try {
            long now = System.currentTimeMillis();
            String token = JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(SUBJECT)
                    .withClaim("userId", user.getId())
                    .withArrayClaim("roles", user.getPerfis().toArray(new Long[0]))
                    .withIssuedAt(new Date(now))
                    .withExpiresAt(new Date(now + ACCESS_TOKEN_MS))
                    .withJWTId(UUID.randomUUID().toString())
                    .sign(algorithm);
            return BEARER_PREFIX + token;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String generateRefreshToken(User user) {
        try {
            long now = System.currentTimeMillis();
            String token = JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject("Refresh")
                    .withClaim("userId", user.getId())
                    .withIssuedAt(new Date(now))
                    .withExpiresAt(new Date(now + REFRESH_TOKEN_MS))
                    .withJWTId(UUID.randomUUID().toString())
                    .sign(algorithm);
            return BEARER_PREFIX + token;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validateToken(String bearerToken) {
        String token = extractToken(bearerToken);
        if (token == null) return false;

        try {
            JWTVerifier verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getExpiresAt() != null && !jwt.getExpiresAt().before(new Date());

        } catch (Exception ignored) {
            return false;
        }
    }

    public User getUserFromToken(String bearerToken) {
        String token = extractToken(bearerToken);
        DecodedJWT decoded = JWT.decode(token);
        String userId = decoded.getClaim("userId").asString();
        List<Long> roles = decoded.getClaim("roles").asList(Long.class);
        User user = User.builder().id(userId).build();
        user.setPerfis(roles);
        return user;
    }

    public String getUserIdFromToken(String bearerToken) {
        String token = extractToken(bearerToken);
        DecodedJWT decoded = JWT.decode(token);
        return decoded.getClaim("userId").asString();
    }

    public List<Long> getUserPerfisFromToken(String bearerToken) {
        String token = extractToken(bearerToken);
        DecodedJWT decoded = JWT.decode(token);
        return decoded.getClaim("roles").asList(Long.class);
    }
}
