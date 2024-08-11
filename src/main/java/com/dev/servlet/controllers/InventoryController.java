package com.dev.servlet.controllers;

import com.dev.servlet.dao.InventoryDAO;
import com.dev.servlet.domain.Inventory;
import com.dev.servlet.interfaces.IController;

import javax.persistence.EntityManager;
import java.util.List;

public final class InventoryController implements IController<Inventory, Long> {

    private static final String CACHE_KEY = "inventories";
    private final InventoryDAO inventoryDao;

    public InventoryController(EntityManager em) {
        this.inventoryDao = new InventoryDAO(em);
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
//        CacheUtil.clear(CACHE_KEY, object.getUser().getToken());
    }

    @Override
    public void delete(Inventory obj) {
        this.inventoryDao.delete(obj);
//        CacheUtil.clear(CACHE_KEY, obj.getUser().getToken());
    }

    @Override
    public void update(Inventory object) {
        this.inventoryDao.update(object);
//        CacheUtil.clear(CACHE_KEY, object.getUser().getToken());
    }

    @Override
    public List<Inventory> findAll(Inventory object) {
        return inventoryDao.findAll(object);
    }
}
