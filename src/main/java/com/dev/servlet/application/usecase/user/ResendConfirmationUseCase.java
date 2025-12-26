package com.dev.servlet.application.usecase.user;

import com.dev.servlet.adapter.out.messaging.Message;
import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.port.in.user.GenerateConfirmationTokenPort;
import com.dev.servlet.application.port.in.user.ResendConfirmationPort;
import com.dev.servlet.application.port.out.AsyncMessagePort;
import com.dev.servlet.application.port.out.confirmtoken.ConfirmationTokenRepositoryPort;
import com.dev.servlet.application.port.out.user.UserRepositoryPort;
import com.dev.servlet.application.transfer.request.ResendConfirmationRequest;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.Status;
import com.dev.servlet.domain.enums.MessageType;
import com.dev.servlet.infrastructure.config.Properties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class ResendConfirmationUseCase implements ResendConfirmationPort {
    @Inject
    private UserRepositoryPort repositoryPort;
    @Inject
    private AsyncMessagePort messagePort;
    @Inject
    private GenerateConfirmationTokenPort generateConfirmationTokenPort;
    @Inject
    private ConfirmationTokenRepositoryPort tokenRepositoryPort;

    @Override
    public void resend(ResendConfirmationRequest request) throws AppException {
        final UUID userId = request.userId();
        log.debug("ResendConfirmationUseCase: resending confirmation for userId {}", userId);

        if (userId == null) {
            log.warn("ResendConfirmationUseCase: userId is blank");
            return;
        }

        Optional<User> maybe = repositoryPort.findById(userId);
        if (maybe.isEmpty()) {
            log.warn("ResendConfirmationUseCase: unknown userId {}", userId);
            return;
        }

        User user = maybe.get();
        if (!Status.PENDING.getValue().equals(user.getStatus())) {
            log.info("ResendConfirmationUseCase: user {} not pending (status={}), skip", userId, user.getStatus());
            return;
        }

        if (tokenRepositoryPort.existsValidTokenForUser(userId)) {
            log.info("ResendConfirmationUseCase: user {} already has a valid confirmation token, skip", userId);
            return;
        }

        String token = generateConfirmationTokenPort.generateFor(user, null);
        String link = Properties.getAppBaseUrl() + "/api/v1/user/confirm?token=" + token;
        String email = user.getCredentials().getLogin();
        String createdAt = OffsetDateTime.now().toString();

        messagePort.send(new Message(MessageType.CONFIRMATION, email, createdAt, link));
    }
}
