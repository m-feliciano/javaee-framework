package com.dev.servlet.application.port.in.stock;

import com.dev.servlet.domain.entity.Inventory;

public interface HasInventoryUseCasePort {
    boolean hasInventory(Inventory inventory, String auth);
}
