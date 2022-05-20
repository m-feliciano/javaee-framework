package controllers;

import java.sql.Connection;
import java.util.List;

import dao.InventoryDAO;
import dto.InventoryDTO;
import entities.Inventory;

public class InventoryController {

    private final InventoryDAO inventoryDAO;
	private Connection conn;

    public InventoryController(Connection conn) {
    	this.conn = conn;
        this.inventoryDAO = new InventoryDAO(conn);
    }

    public void save(Inventory inventory) {
        this.inventoryDAO.save(inventory);
    }

    public void delete(int id) {
        this.inventoryDAO.delete(id);
    }

    public List<InventoryDTO> list() {
        return this.inventoryDAO.list();
    }

    public void update(Inventory inventory) {
        this.inventoryDAO.update(inventory);
    }

    public List<InventoryDTO> findByDescription(String description) {
		return this.inventoryDAO.findByDescription(description);
    }

	public Inventory findById(int id) {
		return this.inventoryDAO.findById(id);
	}

}
