package com.servletstack.application.usecase.user;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.exception.NotFoundException;
import com.servletstack.application.port.in.user.ConfirmEmailUseCase;
import com.servletstack.application.port.out.AsyncMessagePort;
import com.servletstack.application.port.out.confirmtoken.ConfirmationTokenRepositoryPort;
import com.servletstack.application.port.out.user.UserRepositoryPort;
import com.servletstack.application.transfer.request.ConfirmEmailRequest;
import com.servletstack.domain.entity.ConfirmationToken;
import com.servletstack.domain.entity.User;
import com.servletstack.domain.entity.enums.Status;
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
