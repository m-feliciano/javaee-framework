package com.dev.servlet.application.port.in.user;

import com.dev.servlet.application.exception.ApplicationException;

public interface DeleteUserPort {
    void delete(String userId, String auth) throws ApplicationException;
}

