package com.servletstack.adapter.out.security;

import com.servletstack.application.port.out.security.AuthenticationPort;
import com.servletstack.domain.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
@ApplicationScoped
@SuppressWarnings("unchecked")
public class JwtAuthenticationAdapter implements AuthenticationPort {

    private static final String ISSUER = "Servlet";
    private static final String SUBJECT = "Authentication";
    private static final String SUBJECT_REFRESH = "Refresh";
    private static final String USER = "user";
    private static final String ROLES = "roles";
    private static final String BEARER_PREFIX = "Bearer ";

    private final SecretKey key;

    public JwtAuthenticationAdapter() {
        byte[] secret = getJwtSecret();
        this.key = Keys.hmacShaKeyFor(secret);
    }

    private byte[] getJwtSecret() {
        String value = System.getenv("SECURITY_JWT_KEY");
        if (StringUtils.isBlank(value)) {
            log.error("security.jwt.key is not configured");
            throw new IllegalStateException("Missing JWT key");
        }
        return value.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .requireIssuer(ISSUER)
                    .build()
                    .parseSignedClaims(stripBearerPrefix(token));
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public UUID extractUserId(String token) {
        return decode(token, claims -> UUID.fromString(claims.get(USER, String.class)));
    }

    @Override
    public List<Integer> extractRoles(String token) {
        return decode(token, claims -> (List<Integer>) claims.get(ROLES, List.class));
    }

    @Override
    public User extractUser(String token) {
        return decode(token, claims -> {
            UUID userId = UUID.fromString(claims.get(USER, String.class));
            List<Integer> roles = (List<Integer>) claims.get(ROLES, List.class);
            return User.builder()
                    .id(userId)
                    .perfis(roles)
                    .build();
        });
    }

    @Override
    public String generateAccessToken(User user) {
        log.debug("Generating access token for user id {}", user.getId());

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
    }

    @Override
    public String generateRefreshToken(User user) {
        log.debug("Generating refresh token for user id {}", user.getId());
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .issuer(ISSUER)
                .subject(SUBJECT_REFRESH)
                .claim(USER, user.getId())
                .issuedAt(new Date(now))
                .expiration(new Date(now + TimeUnit.DAYS.toMillis(30)))
                .id(UUID.randomUUID().toString())
                .signWith(key)
                .compact();
    }

    @Override
    public String stripBearerPrefix(String token) {
        if (token == null) return null;
        String trimmed = token.trim();
        if (trimmed.startsWith(BEARER_PREFIX))
            return trimmed.substring(BEARER_PREFIX.length()).trim();
        return trimmed;
    }

    private <T> T decode(String token, Function<Claims, T> fn) {
        String tokenPayload = stripBearerPrefix(token);
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .requireIssuer(ISSUER)
                .build()
                .parseSignedClaims(tokenPayload)
                .getPayload();
        return fn.apply(claims);
    }
}
