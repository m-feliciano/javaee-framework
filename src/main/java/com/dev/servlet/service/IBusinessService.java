package com.dev.servlet.service;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.domain.model.Inventory;
import com.dev.servlet.domain.response.ProductResponse;

public interface IBusinessService {
    boolean hasInventory(Inventory inventory, String auth);

    ProductResponse getProductDetail(String id, String auth) throws ServiceException;
}
