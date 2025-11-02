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
import java.util.function.Function;

@Singleton
public class JwtUtil {
    private static final long ACCESS_TOKEN_MS = 7L * 24 * 60 * 60 * 1000; // 7 days
    private static final long REFRESH_TOKEN_MS = 30L * 24 * 60 * 60 * 1000; // 30 days
    private static final String ISSUER = "Servlet";
    private static final String SUBJECT = "Authentication";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String SUB_REFRESH = "Refresh";
    private static final String USER_ID = "userId";
    private static final String ROLES = "roles";

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

    private String extractToken(String token) {
        if (token == null) return null;

        String t = token.trim();
        if (t.startsWith(BEARER_PREFIX))
            return t.substring(BEARER_PREFIX.length()).trim();

        return t;
    }

    public String generateAccessToken(User user) {
        try {
            long now = System.currentTimeMillis();
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(SUBJECT)
                    .withClaim(USER_ID, user.getId())
                    .withArrayClaim(ROLES, user.getPerfis().toArray(new Long[0]))
                    .withIssuedAt(new Date(now))
                    .withExpiresAt(new Date(now + ACCESS_TOKEN_MS))
                    .withJWTId(UUID.randomUUID().toString())
                    .sign(algorithm);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String generateRefreshToken(User user) {
        try {
            long now = System.currentTimeMillis();
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(SUB_REFRESH)
                    .withClaim(USER_ID, user.getId())
                    .withIssuedAt(new Date(now))
                    .withExpiresAt(new Date(now + REFRESH_TOKEN_MS))
                    .withJWTId(UUID.randomUUID().toString())
                    .sign(algorithm);
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

    public User getUser(String token) {
        return decodeAndMap(token, jwt -> {
            String userId = jwt.getClaim(USER_ID).asString();
            List<Long> roles = jwt.getClaim(ROLES).asList(Long.class);
            User user = User.builder().id(userId).build();
            user.setPerfis(roles);
            return user;
        });
    }

    public String getUserId(String token) {
        return decodeAndMap(token, jwt -> jwt.getClaim(USER_ID).asString());
    }

    public List<Long> getRoles(String token) {
        return decodeAndMap(token, jwt -> jwt.getClaim(ROLES).asList(Long.class));
    }

    public <T> T decodeAndMap(String token, Function<DecodedJWT, T> mapper) {
        String plainToken = extractToken(token);
        JWTVerifier verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
        DecodedJWT jwt = verifier.verify(plainToken);
        return mapper.apply(jwt);
    }
}
