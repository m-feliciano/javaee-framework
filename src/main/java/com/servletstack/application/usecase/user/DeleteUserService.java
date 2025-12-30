package com.servletstack.application.usecase.user;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.exception.NotFoundException;
import com.servletstack.application.port.in.user.DeleteUserUseCase;
import com.servletstack.application.port.out.user.UserRepositoryPort;
import com.servletstack.domain.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@ApplicationScoped
public class DeleteUserService implements DeleteUserUseCase {
    @Inject
    private UserRepositoryPort repository;

    public void delete(UUID userId, String auth) throws AppException {
        log.debug("DeleteUserUseCase: deleting user with id {}", userId);
        User user = repository.findById(userId).orElseThrow(NotFoundException::new);
        repository.delete(user);
    }
}
