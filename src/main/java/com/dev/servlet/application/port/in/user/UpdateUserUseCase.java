package com.dev.servlet.application.port.in.user;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.transfer.request.UserRequest;
import com.dev.servlet.application.transfer.response.UserResponse;

public interface UpdateUserUseCase {
    UserResponse update(UserRequest request, String auth) throws AppException;
}

