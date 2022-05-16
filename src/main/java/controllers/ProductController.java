package controllers;

import java.sql.Connection;
import java.util.List;

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
    }

    public void delete(int id) {
        this.productDAO.delete(id);
    }

    public List<Product> list() {
        return this.productDAO.list();
    }

    public List<Product> getProductsByCategoryName(String name) {
        return this.productDAO.getProductsByCategoryName(name);
    }

    public Product findById(int id) {
        return this.productDAO.findById(id);
    }

    public void update(Product prod) {
        this.productDAO.update(prod);
    }

}
