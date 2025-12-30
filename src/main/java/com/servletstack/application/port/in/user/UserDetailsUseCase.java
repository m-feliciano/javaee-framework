package com.servletstack.application.port.in.user;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.transfer.response.UserResponse;

public interface UserDetailsUseCase {
    UserResponse getDetail(String auth) throws AppException;
}