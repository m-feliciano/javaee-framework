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

	public T findById(I id) {
		return this.em.find(clazz, id);
	}

	public T save(T object) {
		try {
			this.em.getTransaction().begin();
			this.em.persist(object);
			this.em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (this.em.getTransaction().isActive()) {
				this.em.getTransaction().rollback();
			}
		} finally {
			if (this.em.isOpen()) {
				this.em.close();
			}
		}
		return object;
	}

	public void update(T object) {
		try {
			this.em.getTransaction().begin();
			this.em.merge(object);
			this.em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (this.em.getTransaction().isActive()) {
				this.em.getTransaction().rollback();
			}
		} finally {
			if (this.em.isOpen()) {
				this.em.close();
			}
		}
	}

	public abstract List<T> findAll(T object);
}
