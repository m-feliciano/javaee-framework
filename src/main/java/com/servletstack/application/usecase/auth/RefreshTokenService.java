package com.servletstack.application.usecase.auth;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.port.in.auth.RefreshTokenUseCase;
import com.servletstack.application.port.out.cache.CachePort;
import com.servletstack.application.port.out.refreshtoken.RefreshTokenRepositoryPort;
import com.servletstack.application.port.out.security.AuthenticationPort;
import com.servletstack.application.transfer.response.RefreshTokenResponse;
import com.servletstack.domain.entity.RefreshToken;
import com.servletstack.domain.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Slf4j
@ApplicationScoped
public class RefreshTokenService implements RefreshTokenUseCase {
    @Inject
    private AuthenticationPort auth;
    @Inject
    private RefreshTokenRepositoryPort repository;
    @Inject
    private CachePort cache;

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) throws AppException {
        log.debug("RefreshTokenUseCase: refreshing token");

        if (!auth.validateToken(refreshToken)) throw new AppException("Invalid refresh token");

        RefreshToken old = validateRefreshToken(refreshToken);
        User user = auth.extractUser(refreshToken);

        String newAccessToken = auth.generateAccessToken(user);
        String newRefreshJwt = auth.generateRefreshToken(user);

        RefreshToken created = createRefreshToken(newRefreshJwt, user);
        log.debug("RefreshTokenUseCase: created new refresh token {}", created.getId());

        old.setRevoked(true);
        old.setReplacedBy(created.getId());
        repository.update(old);
        log.debug("RefreshTokenUseCase: revoking old refresh token {}", old.getId());

        cache.clearAll(user.getId());

        return new RefreshTokenResponse(newAccessToken, newRefreshJwt);
    }

    private RefreshToken validateRefreshToken(String refreshToken) {
        String raw = auth.stripBearerPrefix(refreshToken);
        var maybe = repository.findByToken(raw);
        if (maybe.isEmpty() || maybe.get().getExpiresAt() == null || maybe.get().getExpiresAt().isBefore(Instant.now())) {
            throw new AppException("Refresh token is invalid or revoked");
        }

        return maybe.get();
    }

    private RefreshToken createRefreshToken(String newRefreshJwt, User user) {
        RefreshToken created = RefreshToken.builder()
                .token(auth.stripBearerPrefix(newRefreshJwt))
                .user(user)
                .revoked(false)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(TimeUnit.DAYS.toSeconds(30)))
                .replacedBy(null)
                .build();
        repository.save(created);
        return created;
    }
}
