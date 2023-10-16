package com.dev.servlet.dao;

import java.util.List;

import javax.persistence.EntityManager;

public abstract class BaseDAO<T, I> {

	private final Class<T> clazz;

	protected EntityManager em;

	protected BaseDAO(EntityManager em, Class<T> clazz) {
		this.em = em;
		this.clazz = clazz;
	}

	protected void commitTransaction() {
		this.em.clear();
		this.em.flush();
		this.em.getTransaction().commit();
		this.closeTransaction();
	}

	protected void beginTransaction() {
		this.em.getTransaction().begin();
	}

	protected void closeTransaction() {
		this.em.close();
	}

	public T findById(I id) {
		return this.em.find(clazz, id);
	}

	public T save(T object) {
		this.em.persist(object);
		return object;
	}

	public void update(T object) {
		this.em.merge(object);
	}

	public abstract List<T> findAll(T object);
}
