package controllers;

import java.sql.Connection;
import java.util.List;

import cache.product.ProductCache;
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
		ProductCache.invalidateCache();
	}

	public void delete(int id) {
		this.productDAO.delete(id);
		ProductCache.invalidateCache();
	}

	public List<Product> list() {
		if (ProductCache.isCacheValid()) {
			return ProductCache.getProductsFromCache();
		}
		List<Product> list = this.productDAO.list();
		ProductCache.init(list);
		return list;
	}

	public List<Product> getProductsByCategoryName(String name) {
		return this.productDAO.getProductsByCategoryName(name);
	}

	public Product findById(int id) {
		return this.productDAO.findById(id);
	}

	public void update(Product prod) {
		this.productDAO.update(prod);
		ProductCache.invalidateCache();
	}

}
