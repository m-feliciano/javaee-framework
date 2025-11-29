package com.dev.servlet.application.usecase.user;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.mapper.UserMapper;
import com.dev.servlet.application.port.in.user.ConfirmEmailUseCasePort;
import com.dev.servlet.application.port.out.AuditPort;
import com.dev.servlet.application.port.out.MessagePort;
import com.dev.servlet.application.transfer.request.ConfirmEmailRequest;
import com.dev.servlet.domain.entity.ConfirmationToken;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.Status;
import com.dev.servlet.infrastructure.audit.AuditPayload;
import com.dev.servlet.infrastructure.cache.CacheUtils;
import com.dev.servlet.infrastructure.persistence.repository.ConfirmationTokenRepository;
import com.dev.servlet.infrastructure.persistence.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;

import static com.dev.servlet.shared.util.ThrowableUtils.serviceError;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class ConfirmEmailUseCase implements ConfirmEmailUseCasePort {
    private static final String CACHE_KEY = "userCacheKey";
    @Inject
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Inject
    private UserRepository userRepository;
    @Inject
    private UserMapper userMapper;
    @Inject
    private AuditPort auditPort;
    @Inject
    @Named("emailSender")
    private MessagePort messagePort;

    public void confirm(ConfirmEmailRequest token) throws ApplicationException {
        log.debug("ConfirmEmailUseCase: confirming email with token {}", token.token());

        ConfirmationToken ct = confirmationTokenRepository.findByToken(token.token())
                .orElseThrow(() -> serviceError(HttpServletResponse.SC_NOT_FOUND, "Invalid token."));
        if (ct.isUsed()) {
            throw serviceError(HttpServletResponse.SC_FORBIDDEN, "Token already used.");
        }

        if (ct.getExpiresAt() != null && ct.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw serviceError(HttpServletResponse.SC_FORBIDDEN, "Token expired.");
        }

        User user = userRepository.findById(ct.getUserId())
                .orElseThrow(() -> serviceError(HttpServletResponse.SC_NOT_FOUND, "User not found."));
        user.setStatus(Status.ACTIVE.getValue());

        try {
            user = userRepository.update(user);
            ct.setUsed(true);
            confirmationTokenRepository.update(ct);
            CacheUtils.clear(user.getId(), CACHE_KEY);

        } catch (Exception e) {
            auditPort.failure("user:confirm", null, new AuditPayload<>(token, null));
            throw e;
        }

        messagePort.sendWelcome(user.getCredentials().getLogin());

        auditPort.success("user:confirm", null, new AuditPayload<>(token, userMapper.toResponse(user)));
    }
}
