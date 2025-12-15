package com.dev.servlet.application.port.in.user;

import com.dev.servlet.application.exception.AppException;

public interface DeleteUserPort {
    void delete(String userId, String auth) throws AppException;
}

