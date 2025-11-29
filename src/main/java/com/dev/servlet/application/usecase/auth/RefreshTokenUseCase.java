package com.dev.servlet.application.usecase.auth;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.port.in.auth.RefreshTokenUseCasePort;
import com.dev.servlet.application.port.in.user.UserDetailsUseCasePort;
import com.dev.servlet.application.port.out.AuditPort;
import com.dev.servlet.application.port.out.AuthenticationPort;
import com.dev.servlet.application.transfer.response.RefreshTokenResponse;
import com.dev.servlet.application.transfer.response.UserResponse;
import com.dev.servlet.domain.entity.RefreshToken;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.infrastructure.persistence.repository.RefreshTokenRepository;
import com.dev.servlet.infrastructure.cache.CacheUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class RefreshTokenUseCase implements RefreshTokenUseCasePort {
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private RefreshTokenRepository refreshTokenRepository;
    @Inject
    private AuditPort auditPort;
    @Inject
    private UserDetailsUseCasePort userDetailsUseCasePort;

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) throws ApplicationException {
        log.debug("RefreshTokenUseCase: refreshing token");

        if (!authenticationPort.validateToken(refreshToken)) {
            auditPort.failure("auth:refresh_token", refreshToken, null);
            throw new ApplicationException("Invalid refresh token");
        }

        String raw = authenticationPort.stripBearerPrefix(refreshToken);
        var maybe = refreshTokenRepository.findByToken(raw);
        if (maybe.isEmpty() || maybe.get().getExpiresAt() == null || maybe.get().getExpiresAt().isBefore(Instant.now())) {
            auditPort.failure("auth:refresh_token", refreshToken, null);
            throw new ApplicationException("Refresh token is invalid or revoked");
        }

        RefreshToken old = maybe.get();
        User user = authenticationPort.extractUser(refreshToken);
        UserResponse userResponse = userDetailsUseCasePort.get(user.getId(), refreshToken);

        user.setPerfis(userResponse.getPerfis());
        String newAccessToken = authenticationPort.generateAccessToken(user);
        String newRefreshJwt = authenticationPort.generateRefreshToken(user);

        RefreshToken created = RefreshToken.builder()
                .token(authenticationPort.stripBearerPrefix(newRefreshJwt))
                .user(user)
                .revoked(false)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(TimeUnit.DAYS.toSeconds(30)))
                .replacedBy(null)
                .build();
        refreshTokenRepository.save(created);
        log.debug("RefreshTokenUseCase: created new refresh token {}", created.getId());

        old.setRevoked(true);
        old.setReplacedBy(created.getId());
        refreshTokenRepository.update(old);
        log.debug("RefreshTokenUseCase: revoking old refresh token {}", old.getId());

        CacheUtils.clearAll(user.getId());

        var refreshTokenResponse = new RefreshTokenResponse(newAccessToken, newRefreshJwt);
        auditPort.success("auth:refresh_token", refreshToken, null);
        return refreshTokenResponse;
    }
}
