package com.dev.servlet.application.usecase.auth;

import com.dev.servlet.application.port.in.auth.LogoutPort;
import com.dev.servlet.application.port.out.cache.CachePort;
import com.dev.servlet.application.port.out.refreshtoken.RefreshTokenRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class LogoutUseCase implements LogoutPort {
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private RefreshTokenRepositoryPort repositoryPort;
    @Inject
    private CachePort cachePort;

    @Override
    public void logout(String auth) {
        log.debug("LogoutUseCase: logging out user");

        try {
            String userId = authenticationPort.extractUserId(auth);
            repositoryPort.revokeAll(userId);
            cachePort.clearAll(userId);
        } catch (Exception ignored) {
        }
    }
}
