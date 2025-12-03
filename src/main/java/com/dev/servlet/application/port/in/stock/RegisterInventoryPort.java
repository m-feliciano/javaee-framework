package com.dev.servlet.application.port.in.stock;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.transfer.request.InventoryCreateRequest;
import com.dev.servlet.application.transfer.response.InventoryResponse;

public interface RegisterInventoryPort {
    InventoryResponse register(InventoryCreateRequest request, String auth) throws ApplicationException;
}

