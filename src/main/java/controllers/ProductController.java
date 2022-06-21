package controllers;

import java.util.List;

import javax.persistence.EntityManager;

import dao.ProductDao;
import domain.Category;
import domain.Product;
import utils.cache.CacheUtil;

public class ProductController {

	private final ProductDao productDao;

	public ProductController(EntityManager em) {
		this.productDao = new ProductDao(em);
	}

	public void save(Product product) {
		if (product == null) {
			throw new IllegalArgumentException("The product must not be null.");
		}
		this.productDao.save(product);
		CacheUtil.invalidateProduct();
	}

	public void update(Product prod) {
		this.productDao.update(prod);
		CacheUtil.invalidateProduct();
	}

	public void delete(Long id) {
		this.productDao.delete(id);
		CacheUtil.invalidateProduct();
	}

	public List<Product> findAll() {
		if (CacheUtil.isValidProduct()) {
			return CacheUtil.getProductsFromCache();
		}

		List<Product> list = this.productDao.findAll();
		CacheUtil.initProduct(list);
		return list;
	}

	public Product findById(Long id) {
		if (CacheUtil.isValidProduct()) {
			return findAll().stream().filter(p -> p.getId().equals(id)).findAny().get();
		}

		return this.productDao.findById(id);
	}

	public List<Product> findAllByName(String name) {
		if (CacheUtil.isValidProduct()) {
			return findAll().stream().filter(prod -> prod.getName().toLowerCase().contains(name.toLowerCase())).toList();
		}

		return productDao.findAllByName(name);
	}

	public List<Product> findAllByCategoryName(String name) {
		if (CacheUtil.isValidProduct()) {
			return findAll().stream()
							.filter(prod -> prod.getCategories().stream().map(Category::getName).anyMatch(p -> p.equalsIgnoreCase(name)))
							.toList();
		}

		return productDao.findAllByCategoryName(name);
	}

}
