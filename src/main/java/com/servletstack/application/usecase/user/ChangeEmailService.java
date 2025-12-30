package com.servletstack.application.usecase.user;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.exception.NotFoundException;
import com.servletstack.application.port.in.user.ChangeEmailUseCase;
import com.servletstack.application.port.out.confirmtoken.ConfirmationTokenRepositoryPort;
import com.servletstack.application.port.out.user.UserRepositoryPort;
import com.servletstack.domain.entity.ConfirmationToken;
import com.servletstack.domain.entity.User;
import com.servletstack.shared.util.CloneUtil;
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
