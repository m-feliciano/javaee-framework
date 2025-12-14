package com.dev.servlet.application.port.out.user;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.transfer.request.UserRequest;
import com.dev.servlet.domain.entity.User;

import java.util.Optional;

public interface GetUserPort {
    Optional<User> get(UserRequest userRequest) throws AppException;
}