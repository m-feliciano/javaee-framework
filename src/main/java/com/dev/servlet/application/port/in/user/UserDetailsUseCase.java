package com.dev.servlet.application.port.in.user;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.transfer.response.UserResponse;

public interface UserDetailsUseCase {
    UserResponse getDetail(String auth) throws AppException;
}