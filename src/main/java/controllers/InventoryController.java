package controllers;

import dao.InventoryDao;
import domain.Inventory;

import javax.persistence.EntityManager;
import java.util.List;

public class InventoryController {

    private final InventoryDao inventoryDao;

    public InventoryController(EntityManager em) {
        this.inventoryDao = new InventoryDao(em);
    }

    public void save(Inventory inventory) {
        this.inventoryDao.save(inventory);
    }

    public void delete(Long id) {
        this.inventoryDao.delete(id);
    }

    public List<Inventory> findAll() {
        return this.inventoryDao.findAll();
    }

    public void update(Inventory inventory) {
        this.inventoryDao.update(inventory);
    }

    public List<Inventory> findAllByProductName(String name) {
        return this.inventoryDao.findAllByProductName(name);
    }

    public List<Inventory> findAllByDescription(String description) {
        return this.inventoryDao.findAllByDescription(description);
    }

    public Inventory findById(Long id) {
        return this.inventoryDao.findById(id);
    }

}
