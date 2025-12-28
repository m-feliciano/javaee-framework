package com.dev.servlet.application.usecase.user;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.exception.NotFoundException;
import com.dev.servlet.application.mapper.UserMapper;
import com.dev.servlet.application.port.in.user.UserDetailsUseCase;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.port.out.user.UserRepositoryPort;
import com.dev.servlet.application.transfer.response.UserResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@ApplicationScoped
public class UserDetailsService implements UserDetailsUseCase {
    @Inject
    private UserRepositoryPort repository;
    @Inject
    private UserMapper mapper;
    @Inject
    private AuthenticationPort auth;

    public UserResponse getDetail(String auth) throws AppException {
        UUID userId = this.auth.extractUserId(auth);
        log.debug("UserDetailsUseCase: getting user details with id {}", userId);

        return repository.findById(userId)
                .map(mapper::toResponse)
                .orElseThrow(NotFoundException::new);
    }
}
