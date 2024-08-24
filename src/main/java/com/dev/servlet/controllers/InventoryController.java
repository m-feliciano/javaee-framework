package com.dev.servlet.controllers;

import com.dev.servlet.dao.InventoryDAO;
import com.dev.servlet.domain.Inventory;
import com.dev.servlet.interfaces.IController;

import javax.inject.Inject;
import java.util.List;

public final class InventoryController implements IController<Inventory, Long> {
    private InventoryDAO inventoryDao;

    public InventoryController() {
        // Empty constructor
    }

    @Inject
    public void setDependencies(InventoryDAO inventoryDao) {
        this.inventoryDao = inventoryDao;
    }

    @Override
    public Inventory findById(Long id) {
        return inventoryDao.findById(id);
    }

    @Override
    public Inventory find(Inventory object) {
        return this.inventoryDao.find(object);
    }

    @Override
    public void save(Inventory object) {
        this.inventoryDao.save(object);
    }

    @Override
    public void delete(Inventory obj) {
        this.inventoryDao.delete(obj);
    }

    @Override
    public Inventory update(Inventory object) {
        return this.inventoryDao.update(object);
    }

    @Override
    public List<Inventory> findAll(Inventory object) {
        return inventoryDao.findAll(object);
    }

    public boolean hasInventory(Inventory inventory) {
        return inventoryDao.hasInventory(inventory);
    }
}
