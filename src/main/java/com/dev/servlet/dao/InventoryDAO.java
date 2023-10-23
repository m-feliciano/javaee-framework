package com.dev.servlet.dao;

import java.util.List;

import javax.persistence.EntityManager;

import com.dev.servlet.domain.Inventory;

public class InventoryDAO extends BaseDAO<Inventory, Long> {

	public InventoryDAO(EntityManager em) {
		super(em, Inventory.class);
	}

	/**
	 * Find by name. receives a name and returns all the inventories with that
	 * description.
	 *
	 * @param name
	 * @return
	 */
	public List<Inventory> findAllByProductName(String name) {
		String jpql = "SELECT i FROM Inventory i WHERE LOWER(i.product.name) LIKE CONCAT('%', LOWER(:name), '%')";

		return em.createQuery(jpql, Inventory.class).setParameter("name", name).getResultList();
	}

	/**
	 * Find by description. receives a name and returns all the inventories with
	 * that name.
	 *
	 * @param description
	 * @return
	 */
	public List<Inventory> findAllByDescription(String description) {
		String jpql = "SELECT i FROM Inventory i WHERE LOWER(i.description) LIKE CONCAT('%', LOWER(:description), '%')";

		return em.createQuery(jpql, Inventory.class).setParameter("description", description).getResultList();
	}

	@Override
	public List<Inventory> findAll(Inventory object) {
		// TODO Auto-generated method stub
		return null;
	}

	public void delete(Inventory obj) {
		// TODO Auto-generated method stub
	}

}
