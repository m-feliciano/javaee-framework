package com.dev.servlet.application.usecase.user;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.port.in.user.DeleteUserPort;
import com.dev.servlet.application.port.out.cache.CachePort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.port.out.user.UserRepositoryPort;
import com.dev.servlet.domain.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import static com.dev.servlet.infrastructure.utils.ThrowableUtils.serviceError;

@Slf4j
@ApplicationScoped
public class DeleteUserUseCase implements DeleteUserPort {
    @Inject
    private UserRepositoryPort repositoryPort;
    @Inject
    private AuthenticationPort authPort;
    @Inject
    private CachePort cachePort;

    public void delete(String userId, String auth) throws ApplicationException {
        log.debug("DeleteUserUseCase: deleting user with id {}", userId);

        if (!authPort.extractUserId(auth).trim().equals(userId.trim())) {
            throw serviceError(HttpServletResponse.SC_FORBIDDEN, "User not authorized.");
        }

        User user = repositoryPort.findById(userId)
                .orElseThrow(() -> serviceError(HttpServletResponse.SC_NOT_FOUND, "User not found."));
        repositoryPort.delete(user);

        cachePort.clearAll(user.getId());
    }
}
