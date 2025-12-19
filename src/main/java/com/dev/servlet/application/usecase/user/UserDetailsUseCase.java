package com.dev.servlet.application.usecase.user;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.mapper.UserMapper;
import com.dev.servlet.application.port.in.user.UserDetailsPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.port.out.user.UserRepositoryPort;
import com.dev.servlet.application.transfer.response.UserResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

@Slf4j
@ApplicationScoped
public class UserDetailsUseCase implements UserDetailsPort {
    @Inject
    private UserRepositoryPort repositoryPort;
    @Inject
    private UserMapper userMapper;
    @Inject
    private AuthenticationPort authenticationPort;

    public UserResponse getDetail(String auth) throws AppException {
        String userId = authenticationPort.extractUserId(auth);
        log.debug("UserDetailsUseCase: getting user details with id {}", userId);

        return repositoryPort.findById(userId)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new AppException(SC_NOT_FOUND, "User not found."));
    }
}
