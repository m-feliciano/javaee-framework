package com.dev.servlet.application.port.in.stock;

import com.dev.servlet.domain.entity.Inventory;

public interface HasInventoryUseCase {
    boolean hasInventory(Inventory inventory, String auth);
}
