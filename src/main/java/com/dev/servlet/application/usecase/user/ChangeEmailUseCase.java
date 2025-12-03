package com.dev.servlet.application.usecase.user;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.mapper.UserMapper;
import com.dev.servlet.application.port.in.user.ChangeEmailPort;
import com.dev.servlet.application.port.out.audit.AuditPort;
import com.dev.servlet.application.port.out.cache.CachePort;
import com.dev.servlet.application.port.out.user.UserRepositoryPort;
import com.dev.servlet.application.transfer.response.UserResponse;
import com.dev.servlet.domain.entity.ConfirmationToken;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.infrastructure.persistence.repository.ConfirmationTokenRepository;
import com.dev.servlet.shared.util.CloneUtil;
import com.dev.servlet.shared.vo.AuditPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import static com.dev.servlet.infrastructure.utils.ThrowableUtils.serviceError;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class ChangeEmailUseCase implements ChangeEmailPort {
    @Inject
    private UserMapper userMapper;
    @Inject
    private UserRepositoryPort repositoryPort;
    @Inject
    private AuditPort auditPort;
    @Inject
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Inject
    private CachePort cachePort;

    public void change(String token) throws ApplicationException {
        log.debug("ChangeEmailUseCase: changing email with token {}", token);

        if (StringUtils.isBlank(token)) {
            throw serviceError(HttpServletResponse.SC_BAD_REQUEST, "Token is required");
        }

        ConfirmationToken ct = confirmationTokenRepository.findByToken(token)
                .orElseThrow(() -> serviceError(HttpServletResponse.SC_NOT_FOUND, "Token not found"));

        User user = repositoryPort.findById(ct.getUserId())
                .orElseThrow(() -> serviceError(HttpServletResponse.SC_NOT_FOUND, "User not found"));

        String email = CloneUtil.fromJson(ct.getBody(), String.class);
        user.setLogin(email);
        user = repositoryPort.update(user);

        ct.setUsed(true);
        confirmationTokenRepository.update(ct);

        UserResponse response = userMapper.toResponse(user);
        cachePort.setObject(user.getId(), "userCacheKey", response);
        auditPort.success("user:change_email", null, new AuditPayload<>(token, response));
    }
}
