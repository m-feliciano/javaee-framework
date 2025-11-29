package com.dev.servlet.application.usecase.user;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.mapper.UserMapper;
import com.dev.servlet.application.port.in.user.UserDetailsUseCasePort;
import com.dev.servlet.application.port.out.AuditPort;
import com.dev.servlet.application.port.out.AuthenticationPort;
import com.dev.servlet.application.transfer.response.UserResponse;
import com.dev.servlet.infrastructure.audit.AuditPayload;
import com.dev.servlet.infrastructure.cache.CacheUtils;
import com.dev.servlet.infrastructure.persistence.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.dev.servlet.shared.util.ThrowableUtils.serviceError;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class UserDetailsUseCase implements UserDetailsUseCasePort {
    private static final String CACHE_KEY = "userCacheKey";
    @Inject
    private UserRepository userRepository;
    @Inject
    private UserMapper userMapper;
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private AuditPort auditPort;

    public UserResponse get(String userId, String auth) throws ApplicationException {
        log.debug("UserDetailsUseCase: getting user details with id {}", userId);

        if (!userId.trim().equals(authenticationPort.extractUserId(auth))) {
            throw serviceError(HttpServletResponse.SC_FORBIDDEN, "User not authorized.");
        }

        UserResponse response = CacheUtils.getObject(userId, CACHE_KEY);
        if (response != null) {
            auditPort.success("user:find_by_id", auth, new AuditPayload<>(userId, response));
            return response;
        }

        userRepository.findById(userId)
                .ifPresent(u -> CacheUtils.setObject(userId, CACHE_KEY, userMapper.toResponse(u)));

        response = CacheUtils.getObject(userId, CACHE_KEY);
        auditPort.success("user:find_by_id", auth, new AuditPayload<>(userId, response));
        return response;
    }
}
