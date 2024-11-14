package com.dev.servlet.controllers;

import com.dev.servlet.dao.InventoryDAO;
import com.dev.servlet.pojo.Inventory;

import javax.inject.Inject;
import java.util.Collection;

public final class InventoryController extends BaseController<Inventory, Long> {

    public InventoryDAO inventoryDAO;

    public InventoryController() {
        // Empty constructor
    }

    @Inject
    public InventoryController(InventoryDAO inventoryDAO) {
        super(inventoryDAO);
        this.inventoryDAO = inventoryDAO;
    }

    @Override
    public Collection<Inventory> findAll(Inventory object) {
        return inventoryDAO.findAll(object);
    }

    public boolean hasInventory(Inventory inventory) {
        return inventoryDAO.hasInventory(inventory);
    }
}
