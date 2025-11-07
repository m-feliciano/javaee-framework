package com.dev.servlet.domain.service;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.domain.model.Inventory;
import com.dev.servlet.domain.transfer.request.InventoryCreateRequest;
import com.dev.servlet.domain.transfer.request.InventoryRequest;
import com.dev.servlet.domain.transfer.response.InventoryResponse;

import java.util.List;

public interface IStockService {
    InventoryResponse register(InventoryCreateRequest request, String auth) throws ServiceException;

    List<InventoryResponse> list(InventoryRequest request, String auth) throws ServiceException;

    InventoryResponse getStockDetail(InventoryRequest request, String auth) throws ServiceException;

    InventoryResponse update(InventoryRequest request, String auth) throws ServiceException;

    void delete(InventoryRequest request, String auth) throws ServiceException;

    boolean hasInventory(Inventory request, String auth);
}
