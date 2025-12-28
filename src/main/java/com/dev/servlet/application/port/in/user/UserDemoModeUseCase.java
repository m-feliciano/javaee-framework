package com.dev.servlet.application.port.in.user;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.transfer.request.LoginRequest;
import com.dev.servlet.domain.entity.User;

public interface UserDemoModeUseCase {
    User validateCredentials(LoginRequest credentials) throws AppException;
}
