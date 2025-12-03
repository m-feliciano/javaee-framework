package com.dev.servlet.application.port.in.stock;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.transfer.request.InventoryRequest;

public interface DeleteInventoryPort {
    void delete(InventoryRequest request, String auth) throws ApplicationException;
}

