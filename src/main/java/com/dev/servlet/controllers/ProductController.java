package com.dev.servlet.controllers;

import java.util.List;

import javax.persistence.EntityManager;

import com.dev.servlet.dao.ProductDAO;
import com.dev.servlet.domain.Product;
import com.dev.servlet.interfaces.IController;
import com.dev.servlet.utils.CacheUtil;

public class ProductController implements IController<Product> {

	private final ProductDAO productDao;
	private static final String CACHE_KEY = "products";

	public ProductController(EntityManager em) {
		this.productDao = new ProductDAO(em);
	}

	@Override
	public Product findById(Long id) {
		return this.productDao.findById(id);
	}

	@Override
	public void save(Product product) {
		CacheUtil.clear(CACHE_KEY, product.getUser().getLogin());
		this.productDao.save(product);
	}

	@Override
	public void update(Product product) {
		this.productDao.update(product);
		CacheUtil.clear(CACHE_KEY, product.getUser().getLogin());
	}

	@Override
	public void delete(Product product) {
		this.productDao.delete(product);
		CacheUtil.clear(CACHE_KEY, product.getUser().getLogin());
	}

	@Override
	public List<Product> findAll(Product product) {
		return this.productDao.findAll(product);
	}

}
