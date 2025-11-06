package com.dev.servlet.core.util;

import com.dev.servlet.domain.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.SecretKey;
import javax.inject.Singleton;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
@Singleton
@SuppressWarnings("unchecked")
public class JwtUtil {
    private static final String ISSUER = "Servlet";
    private static final String SUBJECT = "Authentication";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String SUB_REFRESH = "Refresh";
    private static final String USER = "user";
    private static final String ROLES = "roles";

    private final SecretKey key;

    public JwtUtil() {
        byte[] jwtSecret = getJwtSecretBytes();
        key = Keys.hmacShaKeyFor(jwtSecret);
    }

    private byte[] getJwtSecretBytes() {
        String keyStr = PropertiesUtil.getProperty("security.jwt.key");
        if (StringUtils.isBlank(keyStr)) {
            log.error("security.jwt.key is not configured");
            throw new IllegalStateException("Cannot retrieve security.jwt.key");
        }

        return keyStr.getBytes(StandardCharsets.UTF_8);
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
            return Jwts.builder()
                    .issuer(ISSUER)
                    .subject(SUBJECT)
                    .claim(USER, user.getId())
                    .claim(ROLES, user.getPerfis())
                    .issuedAt(new Date(now))
                    .expiration(new Date(now + TimeUnit.DAYS.toMillis(1)))
                    .id(UUID.randomUUID().toString())
                    .signWith(key)
                    .compact();
        } catch (Exception e) {
            log.error("Error generating JWT token", e);
            throw new RuntimeException(e);
        }
    }

    public String generateRefreshToken(User user) {
        try {
            long now = System.currentTimeMillis();
            return Jwts.builder()
                    .issuer(ISSUER)
                    .subject(SUB_REFRESH)
                    .claim(USER, user.getId())
                    .issuedAt(new Date(now))
                    .expiration(new Date(now + TimeUnit.DAYS.toMillis(30)))
                    .id(UUID.randomUUID().toString())
                    .signWith(key)
                    .compact();
        } catch (Exception e) {
            log.error("Error generating refresh JWT token", e);
            throw new RuntimeException(e);
        }
    }

    public boolean validateToken(String bearerToken) {
        String token = extractToken(bearerToken);
        if (token != null) {
            try {
                Jwts.parser()
                        .verifyWith(key)
                        .requireIssuer(ISSUER)
                        .build()
                        .parseSignedClaims(token);
                return true;
            } catch (Exception e) {
                log.warn("Invalid JWT token", e);
            }
        }
        return false;
    }

    public User getUser(String token) {
        return decodeAndMap(token, claims -> {
            String userId = claims.get(USER, String.class);
            List<Integer> roles = (List<Integer>) claims.get(ROLES, List.class);
            User user = new User(userId);
            user.setPerfis(roles);
            return user;
        });
    }

    public String getUserId(String token) {
        return decodeAndMap(token, claims -> claims.get(USER, String.class));
    }

    public List<Integer> getRoles(String token) {
        return (List<Integer>) decodeAndMap(token, claims -> claims.get(ROLES, List.class));
    }

    public <T> T decodeAndMap(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .requireIssuer(ISSUER)
                .build()
                .parseSignedClaims(extractToken(token))
                .getPayload();
        return resolver.apply(claims);
    }
}
