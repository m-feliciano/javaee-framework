package com.dev.servlet.application.usecase.user;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.mapper.UserMapper;
import com.dev.servlet.application.port.in.user.UserDetailsPort;
import com.dev.servlet.application.port.out.cache.CachePort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.port.out.user.UserRepositoryPort;
import com.dev.servlet.application.transfer.response.UserResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

@Slf4j
@ApplicationScoped
public class UserDetailsUseCase implements UserDetailsPort {
    private static final String CACHE_NAMESPACE = "userCacheKey";

    @Inject
    private UserRepositoryPort repositoryPort;
    @Inject
    private UserMapper userMapper;
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private CachePort cachePort;

    public UserResponse getDetail(String userId, String auth) throws AppException {
        log.debug("UserDetailsUseCase: getting user details with id {}", userId);

        if (!userId.trim().equals(authenticationPort.extractUserId(auth))) {
            throw new AppException(SC_FORBIDDEN, "User not authorized.");
        }

        UserResponse response = cachePort.get(CACHE_NAMESPACE, userId);
        if (response == null) {
            response = repositoryPort.findById(userId)
                    .map(userMapper::toResponse)
                    .orElseThrow(() -> new AppException(SC_NOT_FOUND, "User not found."));

            cachePort.set(CACHE_NAMESPACE, userId, response);
        }

        return response;
    }
}
