package com.servletstack.application.port.in.user;

import com.servletstack.application.exception.AppException;

public interface ChangeEmailUseCase {
    void change(String token) throws AppException;
}

