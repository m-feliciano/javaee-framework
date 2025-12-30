package com.servletstack.application.port.in.stock;

import com.servletstack.domain.entity.Inventory;

public interface HasInventoryUseCase {
    boolean hasInventory(Inventory inventory, String auth);
}
