package com.dev.servlet.domain.service;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.domain.model.Inventory;
import com.dev.servlet.domain.transfer.response.ProductResponse;

/**
 * Business service interface providing core business logic operations.
 * 
 * <p>This interface encapsulates essential business rules and operations that
 * span across multiple domains in the application. It provides high-level
 * business operations that coordinate between different entities and enforce
 * business constraints and validations.</p>
 * 
 * @author servlets-team
 * @since 1.0
 */
public interface IBusinessService {
    boolean hasInventory(Inventory inventory, String auth);

    ProductResponse getProductById(String id, String auth) throws ServiceException;
}
