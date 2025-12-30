package com.servletstack.application.port.in.user;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.transfer.request.UserCreateRequest;
import com.servletstack.application.transfer.response.UserResponse;

public interface RegisterUserUseCase {
    UserResponse register(UserCreateRequest userReq) throws AppException;
}
