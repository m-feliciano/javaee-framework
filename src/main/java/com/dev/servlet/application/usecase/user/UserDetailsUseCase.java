package com.dev.servlet.application.usecase.user;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.mapper.UserMapper;
import com.dev.servlet.application.port.in.user.UserDetailsPort;
import com.dev.servlet.application.port.out.cache.CachePort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.port.out.user.UserRepositoryPort;
import com.dev.servlet.application.transfer.response.UserResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import static com.dev.servlet.infrastructure.utils.ThrowableUtils.serviceError;

@Slf4j
@ApplicationScoped
public class UserDetailsUseCase implements UserDetailsPort {
    private static final String CACHE_KEY = "userCacheKey";
    @Inject
    private UserRepositoryPort repositoryPort;
    @Inject
    private UserMapper userMapper;
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private CachePort cachePort;

    public UserResponse get(String userId, String auth) throws ApplicationException {
        log.debug("UserDetailsUseCase: getting user details with id {}", userId);

        if (!userId.trim().equals(authenticationPort.extractUserId(auth))) {
            throw serviceError(HttpServletResponse.SC_FORBIDDEN, "User not authorized.");
        }

        UserResponse response = cachePort.getObject(userId, CACHE_KEY);
        if (response != null) return response;

        repositoryPort.findById(userId)
                .ifPresent(u -> cachePort.setObject(userId, CACHE_KEY, userMapper.toResponse(u)));

        response = cachePort.getObject(userId, CACHE_KEY);
        return response;
    }
}
