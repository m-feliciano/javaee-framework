package com.dev.servlet.application.usecase.auth;

import com.dev.servlet.application.port.in.auth.LogoutUseCasePort;
import com.dev.servlet.application.port.out.AuditPort;
import com.dev.servlet.application.port.out.AuthenticationPort;
import com.dev.servlet.infrastructure.persistence.repository.RefreshTokenRepository;
import com.dev.servlet.infrastructure.cache.CacheUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class LogoutUseCase implements LogoutUseCasePort {
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private RefreshTokenRepository refreshTokenDAO;
    @Inject
    private AuditPort auditPort;

    @Override
    public void logout(String auth) {
        log.debug("LogoutUseCase: logging out user");

        try {
            String userId = authenticationPort.extractUserId(auth);
            refreshTokenDAO.revokeAll(userId);
            CacheUtils.clearAll(userId);
        } catch (Exception ignored) {
        }

        auditPort.success("user:logout", auth, null);
    }
}
