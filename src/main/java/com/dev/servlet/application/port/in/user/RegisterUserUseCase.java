package com.dev.servlet.application.port.in.user;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.transfer.request.UserCreateRequest;
import com.dev.servlet.application.transfer.response.UserResponse;

public interface RegisterUserUseCase {
    UserResponse register(UserCreateRequest userReq) throws AppException;
}
