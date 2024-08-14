package com.dev.servlet.controllers;

import com.dev.servlet.dao.ProductDAO;
import com.dev.servlet.domain.Product;
import com.dev.servlet.interfaces.IController;

import javax.inject.Inject;
import java.util.List;

public class ProductController implements IController<Product, Long> {

    @Inject
    private ProductDAO productDao;

    public ProductController() {
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
    public Product update(Product product) {
        return this.productDao.update(product);
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
