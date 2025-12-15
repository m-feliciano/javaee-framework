package com.dev.servlet.application.usecase.user;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.port.in.user.ChangeEmailPort;
import com.dev.servlet.application.port.out.cache.CachePort;
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

import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

@Slf4j
@ApplicationScoped
public class ChangeEmailUseCase implements ChangeEmailPort {
    @Inject
    private UserRepositoryPort repositoryPort;
    @Inject
    private ConfirmationTokenRepositoryPort tokenRepositoryPort;
    @Inject
    private CachePort cachePort;

    public void change(String token) throws AppException {
        log.debug("ChangeEmailUseCase: changing email with token {}", token);

        if (StringUtils.isBlank(token)) {
            throw new AppException(HttpServletResponse.SC_BAD_REQUEST, "Token is required");
        }

        ConfirmationToken ct = tokenRepositoryPort.findByToken(token)
                .orElseThrow(() -> new AppException(SC_NOT_FOUND, "Token not found"));

        User user = repositoryPort.findById(ct.getUserId())
                .orElseThrow(() -> new AppException(SC_NOT_FOUND, "User not found"));

        String email = CloneUtil.fromJson(ct.getBody(), String.class);
        user.setLogin(email);
        user = repositoryPort.update(user);

        ct.setUsed(true);
        tokenRepositoryPort.update(ct);

        cachePort.clear("userCacheKey", user.getId());
    }
}
