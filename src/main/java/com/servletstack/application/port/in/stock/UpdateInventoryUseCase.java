package com.servletstack.application.port.in.stock;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.transfer.request.InventoryRequest;
import com.servletstack.application.transfer.response.InventoryResponse;

public interface UpdateInventoryUseCase {
    InventoryResponse update(InventoryRequest request, String auth) throws AppException;
}

