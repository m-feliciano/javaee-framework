package com.dev.servlet.application.usecase.user;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.exception.NotFoundException;
import com.dev.servlet.application.port.in.user.ChangeEmailUseCase;
import com.dev.servlet.application.port.out.confirmtoken.ConfirmationTokenRepositoryPort;
import com.dev.servlet.application.port.out.user.UserRepositoryPort;
import com.dev.servlet.domain.entity.ConfirmationToken;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.shared.util.CloneUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@ApplicationScoped
public class ChangeEmailService implements ChangeEmailUseCase {
    @Inject
    private UserRepositoryPort userRepository;
    @Inject
    private ConfirmationTokenRepositoryPort tokenRepository;

    public void change(String token) throws AppException {
        log.debug("ChangeEmailUseCase: changing email with token {}", token);

        if (StringUtils.isBlank(token)) {
            throw new AppException(HttpServletResponse.SC_BAD_REQUEST, "Token is required");
        }

        ConfirmationToken ct = tokenRepository.findByToken(token).orElseThrow(NotFoundException::new);
        User user = userRepository.findById(ct.getUserId()).orElseThrow(NotFoundException::new);

        String email = CloneUtil.fromJson(ct.getBody(), String.class);
        user.setLogin(email);
        userRepository.update(user);

        ct.setUsed(true);
        tokenRepository.update(ct);
    }
}
