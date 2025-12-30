package com.servletstack.application.port.in.user;

import com.servletstack.application.exception.AppException;

import java.util.UUID;

public interface DeleteUserUseCase {
    void delete(UUID userId, String auth) throws AppException;
}

