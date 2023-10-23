package com.dev.servlet.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.dev.servlet.domain.Category;
import com.dev.servlet.domain.enums.Status;

public class CategoryDAO extends BaseDAO<Category, Long> {

	public CategoryDAO(EntityManager em) {
		super(em, Category.class);
	}

	@Override
	public List<Category> findAll(Category object) {
		String jpql = "SELECT i FROM Category i where i.status <> :status";

		return em.createQuery(jpql, Category.class).setParameter("status", Status.DELETED.getDescription())
				.getResultList();
	}

	public void delete(Category cat) {
		Query query = em.createQuery("UPDATE Category SET status = :status WHERE id = :id")
				.setParameter("status", Status.DELETED.getDescription()).setParameter("id", cat.getId());

		query.executeUpdate();
	}
}
