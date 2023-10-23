package com.dev.servlet.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.dev.servlet.domain.Category;
import com.dev.servlet.domain.Product;

public class ProductDAO extends BaseDAO<Product, Long> {

	public static final String STATUS = "status";
	public static final String ID = "id";
	public static final String USER = "user";
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	public static final String CATEGORY = "category";

	public ProductDAO(EntityManager em) {
		super(em, Product.class);
	}

	/**
	 * Find all by category category
	 *
	 * @param category
	 * @return
	 */
	public List<Product> findAllByCategory(Category category) {
		String jpql = "SELECT p FROM Product p WHERE LOWER(p.category.name) LIKE LOWER(CONCAT('%', :name, '%'))";
		List<Product> resultList = em.createQuery(jpql, Product.class).setParameter(NAME, category.getName())
				.getResultList();
		return resultList;
	}

	/**
	 * Find all by user/product
	 *
	 * @param product
	 * @return
	 */
	@Override
	public List<Product> findAll(Product product) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Product> query = builder.createQuery(Product.class);
		Root<Product> root = query.from(Product.class);

		query.where(builder.equal(root.get("user"), product.getUser())).select(root).distinct(true);

		return em.createQuery(query).getResultList();
	}

	public void delete(Product product) {
		// TODO Auto-generated method stub
	}

}
