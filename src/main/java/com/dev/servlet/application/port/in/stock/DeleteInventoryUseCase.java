package com.dev.servlet.application.port.in.stock;

import com.dev.servlet.application.exception.AppException;

import java.util.UUID;

public interface DeleteInventoryUseCase {
    void delete(UUID uuid) throws AppException;
}

