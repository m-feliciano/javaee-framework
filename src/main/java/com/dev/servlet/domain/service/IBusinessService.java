package com.dev.servlet.domain.service;
import com.dev.servlet.domain.model.Inventory;
import com.dev.servlet.domain.model.Product;
import com.dev.servlet.domain.model.User;

public interface IBusinessService {
    boolean hasInventory(Inventory inventory);
    Product getProductById(Long id, User user);
}
