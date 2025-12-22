package com.dev.servlet.application.usecase.user;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.port.in.user.ConfirmEmailPort;
import com.dev.servlet.application.port.out.MessagePort;
import com.dev.servlet.application.port.out.confirmtoken.ConfirmationTokenRepositoryPort;
import com.dev.servlet.application.port.out.user.UserRepositoryPort;
import com.dev.servlet.application.transfer.request.ConfirmEmailRequest;
import com.dev.servlet.domain.entity.ConfirmationToken;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.Status;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;

import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

@Slf4j
@ApplicationScoped
public class ConfirmEmailUseCase implements ConfirmEmailPort {
    @Inject
    private ConfirmationTokenRepositoryPort tokenRepositoryPort;
    @Inject
    private UserRepositoryPort repositoryPort;
    @Inject
    @Named("smtpEmailSender")
    private MessagePort messagePort;

    public void confirm(ConfirmEmailRequest token) throws AppException {
        log.debug("ConfirmEmailUseCase: confirming email with token {}", token.token());

        ConfirmationToken ct = tokenRepositoryPort.findByToken(token.token())
                .orElseThrow(() -> new AppException(SC_NOT_FOUND, "Invalid token."));

        User user = repositoryPort.findById(ct.getUserId())
                .orElseThrow(() -> new AppException(SC_NOT_FOUND, "User not found."));

        user.setStatus(Status.ACTIVE.getValue());
        user = repositoryPort.update(user);

        ct.setUsed(true);
        tokenRepositoryPort.update(ct);
        messagePort.sendWelcome(user.getCredentials().getLogin());
    }
}
