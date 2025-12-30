package com.servletstack.application.port.in.stock;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.transfer.request.InventoryCreateRequest;
import com.servletstack.application.transfer.response.InventoryResponse;

public interface RegisterInventoryUseCase {
    InventoryResponse register(InventoryCreateRequest request, String auth) throws AppException;
}

