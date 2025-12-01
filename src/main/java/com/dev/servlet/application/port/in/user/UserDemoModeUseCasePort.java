package com.dev.servlet.application.port.in.user;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.transfer.request.LoginRequest;
import com.dev.servlet.domain.entity.User;

public interface UserDemoModeUseCasePort {
    User validateCredentials(LoginRequest credentials) throws ApplicationException;
}
