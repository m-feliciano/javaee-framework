package com.dev.servlet.application.port.in.user;

import com.dev.servlet.application.exception.AppException;

import java.util.UUID;

public interface DeleteUserUseCase {
    void delete(UUID userId, String auth) throws AppException;
}

