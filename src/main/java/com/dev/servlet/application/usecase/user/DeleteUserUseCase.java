package com.dev.servlet.application.usecase.user;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.port.in.user.DeleteUserPort;
import com.dev.servlet.application.port.out.audit.AuditPort;
import com.dev.servlet.application.port.out.cache.CachePort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.port.out.user.UserRepositoryPort;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.shared.vo.AuditPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.dev.servlet.infrastructure.utils.ThrowableUtils.serviceError;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class DeleteUserUseCase implements DeleteUserPort {
    @Inject
    private UserRepositoryPort repositoryPort;
    @Inject
    private AuditPort auditPort;
    @Inject
    private AuthenticationPort authPort;
    @Inject
    private CachePort cachePort;

    public void delete(String userId, String auth) throws ApplicationException {
        log.debug("DeleteUserUseCase: deleting user with id {}", userId);

        if (!authPort.extractUserId(auth).trim().equals(userId.trim())) {
            throw serviceError(HttpServletResponse.SC_FORBIDDEN, "User not authorized.");
        }

        try {
            User user = repositoryPort.findById(userId)
                    .orElseThrow(() -> serviceError(HttpServletResponse.SC_NOT_FOUND, "User not found."));
            repositoryPort.delete(user);

            cachePort.clearAll(user.getId());
            auditPort.success("user:delete", auth, new AuditPayload<>(userId, null));
        } catch (Exception e) {
            auditPort.success("user:delete", auth, new AuditPayload<>(userId, e.getMessage()));
            throw e;
        }
    }
}
