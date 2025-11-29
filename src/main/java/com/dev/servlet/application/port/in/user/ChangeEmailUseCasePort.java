package com.dev.servlet.application.port.in.user;

import com.dev.servlet.application.exception.ApplicationException;

public interface ChangeEmailUseCasePort {
    void change(String token) throws ApplicationException;
}

