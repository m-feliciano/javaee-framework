package com.dev.servlet.application.port.in.user;

import com.dev.servlet.application.exception.AppException;

public interface ChangeEmailUseCase {
    void change(String token) throws AppException;
}

