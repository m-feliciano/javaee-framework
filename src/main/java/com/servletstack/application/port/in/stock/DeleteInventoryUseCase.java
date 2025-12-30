package com.servletstack.application.port.in.stock;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.transfer.request.InventoryRequest;

public interface DeleteInventoryUseCase {
    void delete(InventoryRequest request, String auth) throws AppException;
}

