package com.dev.servlet.application.usecase.user;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.port.in.user.DeleteUserPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.port.out.user.UserRepositoryPort;
import com.dev.servlet.domain.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

@Slf4j
@ApplicationScoped
public class DeleteUserUseCase implements DeleteUserPort {
    @Inject
    private UserRepositoryPort repositoryPort;
    @Inject
    private AuthenticationPort authPort;

    public void delete(String userId, String auth) throws AppException {
        log.debug("DeleteUserUseCase: deleting user with id {}", userId);

        if (!authPort.extractUserId(auth).trim().equals(userId.trim())) {
            throw new AppException(SC_FORBIDDEN, "User not authorized.");
        }

        User user = repositoryPort.findById(userId)
                .orElseThrow(() -> new AppException(SC_NOT_FOUND, "User not found."));
        repositoryPort.delete(user);
    }
}
