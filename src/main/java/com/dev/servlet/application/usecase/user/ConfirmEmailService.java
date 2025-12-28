package com.dev.servlet.application.usecase.user;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.exception.NotFoundException;
import com.dev.servlet.application.port.in.user.ConfirmEmailUseCase;
import com.dev.servlet.application.port.out.AsyncMessagePort;
import com.dev.servlet.application.port.out.confirmtoken.ConfirmationTokenRepositoryPort;
import com.dev.servlet.application.port.out.user.UserRepositoryPort;
import com.dev.servlet.application.transfer.request.ConfirmEmailRequest;
import com.dev.servlet.domain.entity.ConfirmationToken;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.Status;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class ConfirmEmailService implements ConfirmEmailUseCase {
    @Inject
    private ConfirmationTokenRepositoryPort tokenRepository;
    @Inject
    private UserRepositoryPort userRepository;
    @Inject
    private AsyncMessagePort message;

    public void confirm(ConfirmEmailRequest token) throws AppException {
        log.debug("ConfirmEmailUseCase: confirming email with token {}", token.token());

        ConfirmationToken ct = tokenRepository.findByToken(token.token()).orElseThrow(NotFoundException::new);
        User user = userRepository.findById(ct.getUserId()).orElseThrow(NotFoundException::new);

        user.setStatus(Status.ACTIVE.getValue());
        user = userRepository.update(user);

        ct.setUsed(true);
        tokenRepository.update(ct);
        message.sendWelcome(user.getCredentials().getLogin());
    }
}
