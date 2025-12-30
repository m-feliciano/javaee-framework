package com.servletstack.application.port.in.user;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.transfer.request.UserRequest;
import com.servletstack.application.transfer.response.UserResponse;

public interface UpdateUserUseCase {
    UserResponse update(UserRequest request, String auth) throws AppException;
}

