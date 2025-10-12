package com.dev.servlet.domain.service;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.domain.model.Inventory;
import com.dev.servlet.domain.transfer.response.InventoryResponse;
import com.dev.servlet.domain.transfer.request.InventoryCreateRequest;
import com.dev.servlet.domain.transfer.request.InventoryRequest;

import java.util.List;

/**
 * Service interface for managing inventory and stock operations in the servlet application.
 * 
 * <p>This interface defines the contract for all stock-related business operations,
 * including inventory management, stock level monitoring, and availability checking.
 * It handles the relationship between products and their available quantities,
 * ensuring proper stock control and inventory tracking throughout the system.</p>
 * 
 * @author servlets-team
 * @since 1.0
 */
public interface IStockService {
    InventoryResponse create(InventoryCreateRequest request, String auth) throws ServiceException;

    List<InventoryResponse> list(InventoryRequest request, String auth) throws ServiceException;

    InventoryResponse findById(InventoryRequest request, String auth) throws ServiceException;

    InventoryResponse update(InventoryRequest request, String auth) throws ServiceException;

    void delete(InventoryRequest request, String auth) throws ServiceException;

    boolean hasInventory(Inventory request, String auth);
}
