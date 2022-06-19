package controllers;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import cache.CacheUtil;
import dao.ProductDAO;
import entities.Product;

public class ProductController {

	private final ProductDAO productDAO;
	private final Connection conn;

	public ProductController(Connection conn) {
		this.conn = conn;
		this.productDAO = new ProductDAO(conn);
	}

	public void save(Product product) {
		this.productDAO.save(product);
		CacheUtil.invalidateProduct();
	}

	public void delete(int id) {
		this.productDAO.delete(id);
		CacheUtil.invalidateProduct();
	}

	public List<Product> list() {
		if (CacheUtil.isValidProduct()) {
			return CacheUtil.getProductsFromCache();
		}
		List<Product> list = this.productDAO.list();
		CacheUtil.initProduct(list);
		return list;
	}

	public List<Product> getProductsByCategory(String name) {
		return this.productDAO.getProductsByCategory(name);
	}

	public Product findById(int id) {
		if(CacheUtil.isValidProduct()) {
			Optional<Product> product = this.list().stream()
					.filter(p -> p.getId() == id)
					.findAny();
			return product.isPresent() ? product.get() : null;
		}
		return this.productDAO.findById(id);
	}

	public void update(Product prod) {
		this.productDAO.update(prod);
		CacheUtil.invalidateProduct();
	}

	public List<Product> findAllByName(String name) {
		return this.list().stream()
				.filter(prod -> prod.getName().toLowerCase()
						.contains(name.toLowerCase()))
				.toList();
	}

}
