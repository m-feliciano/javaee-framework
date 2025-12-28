package com.dev.servlet.application.port.in.stock;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.transfer.request.InventoryRequest;
import com.dev.servlet.application.transfer.response.InventoryResponse;

public interface UpdateInventoryUseCase {
    InventoryResponse update(InventoryRequest request, String auth) throws AppException;
}

