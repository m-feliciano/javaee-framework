package com.dev.servlet.application.port.in.user;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.transfer.request.UserRequest;
import com.dev.servlet.domain.entity.User;

import java.util.Optional;

public interface GetUserPort {
    Optional<User> get(UserRequest userRequest) throws ApplicationException;
}