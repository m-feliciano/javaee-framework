package com.servletstack.application.usecase.auth;

import com.servletstack.application.port.in.auth.LogoutUseCase;
import com.servletstack.application.port.out.cache.CachePort;
import com.servletstack.application.port.out.refreshtoken.RefreshTokenRepositoryPort;
import com.servletstack.application.port.out.security.AuthenticationPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@ApplicationScoped
public class LogoutService implements LogoutUseCase {
    @Inject
    private AuthenticationPort auth;
    @Inject
    private RefreshTokenRepositoryPort repository;
    @Inject
    private CachePort cache;

    @Override
    public void logout(String auth) {
        log.debug("LogoutUseCase: logging out user");

        try {
            UUID userId = this.auth.extractUserId(auth);
            repository.revokeAll(userId);
            cache.clearAll(userId);
        } catch (Exception ignored) {
        }
    }
}
