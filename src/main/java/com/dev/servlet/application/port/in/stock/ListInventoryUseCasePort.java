package com.dev.servlet.application.port.in.stock;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.transfer.request.InventoryRequest;
import com.dev.servlet.application.transfer.response.InventoryResponse;

import java.util.List;

public interface ListInventoryUseCasePort {
    List<InventoryResponse> list(InventoryRequest request, String auth) throws ApplicationException;
}

