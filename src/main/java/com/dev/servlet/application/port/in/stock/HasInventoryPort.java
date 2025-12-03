package com.dev.servlet.application.port.in.stock;

import com.dev.servlet.domain.entity.Inventory;

public interface HasInventoryPort {
    boolean hasInventory(Inventory inventory, String auth);
}
