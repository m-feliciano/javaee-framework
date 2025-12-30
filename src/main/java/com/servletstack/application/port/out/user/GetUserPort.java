package com.servletstack.application.port.out.user;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.transfer.request.UserRequest;
import com.servletstack.domain.entity.User;

import java.util.Optional;

public interface GetUserPort {
    Optional<User> get(UserRequest userRequest) throws AppException;
}