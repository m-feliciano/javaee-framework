package com.dev.servlet.controllers;

import com.dev.servlet.dao.ProductDAO;
import com.dev.servlet.domain.Product;
import com.dev.servlet.interfaces.IController;

import javax.persistence.EntityManager;
import java.util.List;

public class ProductController implements IController<Product, Long> {
    private final ProductDAO productDao;

    public ProductController(EntityManager em) {
        this.productDao = new ProductDAO(em);
    }

    @Override
    public Product findById(Long id) {
        return this.productDao.findById(id);
    }

    @Override
    public Product find(Product product) {
        return this.productDao.find(product);
    }

    @Override
    public void save(Product product) {
        this.productDao.save(product);
    }

    @Override
    public void update(Product product) {
        this.productDao.update(product);
    }

    @Override
    public void delete(Product product) {
        this.productDao.delete(product);
    }

    @Override
    public List<Product> findAll(Product product) {
        return this.productDao.findAll(product);
    }

}
