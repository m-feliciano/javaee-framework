package com.dev.servlet.application.port.in.user;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.transfer.request.UserCreateRequest;
import com.dev.servlet.application.transfer.response.UserResponse;

public interface RegisterUserPort {
    UserResponse register(UserCreateRequest userReq) throws ApplicationException;
}
