package com.dev.servlet.controllers;

import com.dev.servlet.dao.ProductDAO;
import com.dev.servlet.interfaces.IController;
import com.dev.servlet.interfaces.IPagination;
import com.dev.servlet.pojo.Product;
import com.dev.servlet.pojo.records.Pagable;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

public class ProductController implements IController<Product, Long>, IPagination<Product> {
    private ProductDAO productDao;

    public ProductController() {
        // Empty constructor
    }

    @Inject
    public void setDependencies(ProductDAO productDao) {
        this.productDao = productDao;
    }

//    @Override
//    public Product findById(Long id) {
//        return this.productDao.findById(id);
//    }

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

    public Long getTotalResults(Product product) {
        return this.productDao.getTotalResults(product);
    }

    @Override
    public Collection<Product> findAll(Product object, Pagable pagable) {
        return this.productDao.findAll(object, pagable);
    }
}
