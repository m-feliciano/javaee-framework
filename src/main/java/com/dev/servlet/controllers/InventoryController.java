package com.dev.servlet.controllers;

import java.util.List;

import javax.persistence.EntityManager;

import com.dev.servlet.dao.InventoryDAO;
import com.dev.servlet.domain.Inventory;
import com.dev.servlet.interfaces.IController;

public final class InventoryController implements IController<Inventory> {

	private final InventoryDAO inventoryDao;

	public InventoryController(EntityManager em) {
		this.inventoryDao = new InventoryDAO(em);
	}

	@Override
	public Inventory findById(Long id) {
		return inventoryDao.findById(id);
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
	public void update(Inventory object) {
		this.inventoryDao.update(object);
	}

	@Override
	public List<Inventory> findAll(Inventory object) {
		return inventoryDao.findAll(object);
	}

	public List<Inventory> findAllByProductName(String name) {
		return this.inventoryDao.findAllByProductName(name);
	}

	public List<Inventory> findAllByDescription(String description) {
		return this.inventoryDao.findAllByDescription(description);
	}
}
