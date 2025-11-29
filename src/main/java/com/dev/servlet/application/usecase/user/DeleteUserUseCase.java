package com.dev.servlet.application.usecase.user;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.port.in.user.DeleteUserUseCasePort;
import com.dev.servlet.application.port.out.AuditPort;
import com.dev.servlet.application.port.out.AuthenticationPort;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.infrastructure.persistence.repository.UserRepository;
import com.dev.servlet.infrastructure.audit.AuditPayload;
import com.dev.servlet.infrastructure.cache.CacheUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.dev.servlet.shared.util.ThrowableUtils.serviceError;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class DeleteUserUseCase implements DeleteUserUseCasePort {
    @Inject
    private UserRepository userRepository;
    @Inject
    private AuditPort auditPort;
    @Inject
    private AuthenticationPort authPort;

    public void delete(String userId, String auth) throws ApplicationException {
        log.debug("DeleteUserUseCase: deleting user with id {}", userId);

        if (!authPort.extractUserId(auth).trim().equals(userId.trim())) {
            throw serviceError(HttpServletResponse.SC_FORBIDDEN, "User not authorized.");
        }

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> serviceError(HttpServletResponse.SC_NOT_FOUND, "User not found."));
            userRepository.delete(user);

            CacheUtils.clearAll(user.getId());
            auditPort.success("user:delete", auth, new AuditPayload<>(userId, null));
        } catch (Exception e) {
            auditPort.success("user:delete", auth, new AuditPayload<>(userId, e.getMessage()));
            throw e;
        }
    }
}
