package com.servletstack.application.usecase.user;

import com.servletstack.adapter.out.messaging.Message;
import com.servletstack.application.exception.AppException;
import com.servletstack.application.port.in.user.GenerateConfirmationTokenUseCase;
import com.servletstack.application.port.in.user.ResendConfirmationUseCase;
import com.servletstack.application.port.out.AsyncMessagePort;
import com.servletstack.application.port.out.confirmtoken.ConfirmationTokenRepositoryPort;
import com.servletstack.application.port.out.user.UserRepositoryPort;
import com.servletstack.application.transfer.request.ResendConfirmationRequest;
import com.servletstack.domain.entity.User;
import com.servletstack.domain.entity.enums.Status;
import com.servletstack.domain.enums.MessageType;
import com.servletstack.infrastructure.config.Properties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class ResendConfirmationService implements ResendConfirmationUseCase {
    @Inject
    private UserRepositoryPort repository;
    @Inject
    private AsyncMessagePort messagePort;
    @Inject
    private GenerateConfirmationTokenUseCase generateConfirmationTokenUseCase;
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

        Optional<User> maybe = repository.findById(userId);
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

        String token = generateConfirmationTokenUseCase.generateFor(user, null);
        String link = Properties.getAppBaseUrl() + "/api/v1/user/confirm?token=" + token;
        String email = user.getCredentials().getLogin();
        String createdAt = OffsetDateTime.now().toString();

        messagePort.send(new Message(MessageType.CONFIRMATION, email, createdAt, link));
    }
}
