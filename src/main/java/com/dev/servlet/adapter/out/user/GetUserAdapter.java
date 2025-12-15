package com.dev.servlet.adapter.out.user;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.port.out.user.GetUserPort;
import com.dev.servlet.application.port.out.user.UserRepositoryPort;
import com.dev.servlet.application.transfer.request.UserRequest;
import com.dev.servlet.domain.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@ApplicationScoped
public class GetUserAdapter implements GetUserPort {
    @Inject
    private UserRepositoryPort repositoryPort;


    public Optional<User> get(UserRequest request) throws AppException {
        log.debug("GetUserUseCase: getting user details with id {}", request.login());
        return repositoryPort.find(new User(request.login(), request.password()));
    }
}
