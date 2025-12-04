package com.dev.servlet.application.usecase.auth;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.port.in.auth.RefreshTokenPort;
import com.dev.servlet.application.port.in.user.UserDetailsPort;
import com.dev.servlet.application.port.out.cache.CachePort;
import com.dev.servlet.application.port.out.refreshtoken.RefreshTokenRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.response.RefreshTokenResponse;
import com.dev.servlet.application.transfer.response.UserResponse;
import com.dev.servlet.domain.entity.RefreshToken;
import com.dev.servlet.domain.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Slf4j
@ApplicationScoped
public class RefreshTokenUseCase implements RefreshTokenPort {
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private RefreshTokenRepositoryPort repositoryPort;
    @Inject
    private UserDetailsPort userDetailsPort;
    @Inject
    private CachePort cachePort;

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) throws ApplicationException {
        log.debug("RefreshTokenUseCase: refreshing token");

        if (!authenticationPort.validateToken(refreshToken)) throw new ApplicationException("Invalid refresh token");

        RefreshToken old = validateRefreshToken(refreshToken);
        User user = authenticationPort.extractUser(refreshToken);
        UserResponse userResponse = userDetailsPort.get(user.getId(), refreshToken);

        user.setPerfis(userResponse.getPerfis());
        String newAccessToken = authenticationPort.generateAccessToken(user);
        String newRefreshJwt = authenticationPort.generateRefreshToken(user);

        RefreshToken created = createRefreshToken(newRefreshJwt, user);
        log.debug("RefreshTokenUseCase: created new refresh token {}", created.getId());

        old.setRevoked(true);
        old.setReplacedBy(created.getId());
        repositoryPort.update(old);
        log.debug("RefreshTokenUseCase: revoking old refresh token {}", old.getId());

        cachePort.clearAll(user.getId());

        return new RefreshTokenResponse(newAccessToken, newRefreshJwt);
    }

    private RefreshToken validateRefreshToken(String refreshToken) {
        String raw = authenticationPort.stripBearerPrefix(refreshToken);
        var maybe = repositoryPort.findByToken(raw);
        if (maybe.isEmpty() || maybe.get().getExpiresAt() == null || maybe.get().getExpiresAt().isBefore(Instant.now())) {
            throw new ApplicationException("Refresh token is invalid or revoked");
        }

        return maybe.get();
    }

    private RefreshToken createRefreshToken(String newRefreshJwt, User user) {
        RefreshToken created = RefreshToken.builder()
                .token(authenticationPort.stripBearerPrefix(newRefreshJwt))
                .user(user)
                .revoked(false)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(TimeUnit.DAYS.toSeconds(30)))
                .replacedBy(null)
                .build();
        repositoryPort.save(created);
        return created;
    }
}
