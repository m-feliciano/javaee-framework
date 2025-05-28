package com.dev.servlet.domain.service;
import com.dev.servlet.domain.transfer.dto.InventoryDTO;
import com.dev.servlet.domain.transfer.request.Request;
import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.domain.model.Inventory;
import java.util.List;

public interface IStockService {
    InventoryDTO create(Request request) throws ServiceException;
    List<InventoryDTO> list(Request request);
    InventoryDTO findById(Request request) throws ServiceException;
    InventoryDTO update(Request request) throws ServiceException;
    boolean delete(Request request) throws ServiceException;
    boolean hasInventory(Inventory inventory);
}
