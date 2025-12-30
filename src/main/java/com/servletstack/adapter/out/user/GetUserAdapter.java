package com.servletstack.adapter.out.user;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.port.out.user.GetUserPort;
import com.servletstack.application.port.out.user.UserRepositoryPort;
import com.servletstack.application.transfer.request.UserRequest;
import com.servletstack.domain.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@ApplicationScoped
public class GetUserAdapter implements GetUserPort {
    @Inject
    private UserRepositoryPort repository;


    public Optional<User> get(UserRequest request) throws AppException {
        log.debug("GetUserUseCase: getting user details with id {}", request.login());
        return repository.find(new User(request.login(), request.password()));
    }
}
