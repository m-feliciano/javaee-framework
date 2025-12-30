package com.servletstack.application.port.in.user;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.transfer.request.LoginRequest;
import com.servletstack.domain.entity.User;

public interface UserDemoModeUseCase {
    User validateCredentials(LoginRequest credentials) throws AppException;
}
