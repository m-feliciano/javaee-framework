package com.dev.servlet.application.usecase.user;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.port.in.user.ConfirmEmailPort;
import com.dev.servlet.application.port.out.MessagePort;
import com.dev.servlet.application.port.out.cache.CachePort;
import com.dev.servlet.application.port.out.confirmtoken.ConfirmationTokenRepositoryPort;
import com.dev.servlet.application.port.out.user.UserRepositoryPort;
import com.dev.servlet.application.transfer.request.ConfirmEmailRequest;
import com.dev.servlet.domain.entity.ConfirmationToken;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.Status;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;

import static com.dev.servlet.infrastructure.utils.ThrowableUtils.serviceError;

@Slf4j
@ApplicationScoped
public class ConfirmEmailUseCase implements ConfirmEmailPort {
    @Inject
    private ConfirmationTokenRepositoryPort tokenRepositoryPort;
    @Inject
    private UserRepositoryPort repositoryPort;
    @Inject
    @Named("emailSender")
    private MessagePort messagePort;
    @Inject
    private CachePort cachePort;

    public void confirm(ConfirmEmailRequest token) throws ApplicationException {
        log.debug("ConfirmEmailUseCase: confirming email with token {}", token.token());

        ConfirmationToken ct = tokenRepositoryPort.findByToken(token.token())
                .orElseThrow(() -> serviceError(HttpServletResponse.SC_NOT_FOUND, "Invalid token."));
        if (ct.isUsed()) {
            throw serviceError(HttpServletResponse.SC_FORBIDDEN, "Token already used.");
        }

        if (ct.getExpiresAt() != null && ct.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw serviceError(HttpServletResponse.SC_FORBIDDEN, "Token expired.");
        }

        User user = repositoryPort.findById(ct.getUserId())
                .orElseThrow(() -> serviceError(HttpServletResponse.SC_NOT_FOUND, "User not found."));
        user.setStatus(Status.ACTIVE.getValue());
        user = repositoryPort.update(user);

        ct.setUsed(true);
        tokenRepositoryPort.update(ct);

        cachePort.clear(user.getId(), "userCacheKey");
        messagePort.sendWelcome(user.getCredentials().getLogin());
    }
}
