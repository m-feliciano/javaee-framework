package com.dev.servlet.application.port.in.stock;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.transfer.response.InventoryResponse;

import java.util.UUID;

public interface GetInventoryDetailUseCase {
    InventoryResponse get(UUID uuid) throws AppException;
}

