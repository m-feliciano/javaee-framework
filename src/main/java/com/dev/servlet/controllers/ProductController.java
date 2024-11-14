package com.dev.servlet.controllers;

import com.dev.servlet.dao.ProductDAO;
import com.dev.servlet.pojo.Product;
import com.dev.servlet.pojo.records.Pagination;

import javax.inject.Inject;
import java.util.Collection;

public final class ProductController extends BaseController<Product, Long> {

    public ProductDAO productDAO;

    public ProductController() {
        // Empty constructor
    }

    @Inject
    public ProductController(ProductDAO productDAO) {
        super(productDAO);
        this.productDAO = productDAO;
    }

    @Override
    public Collection<Product> findAll(Product product) {
        return this.productDAO.findAll(product);
    }

    public Long getTotalResults(Product product) {
        return this.productDAO.getTotalResults(product);
    }

    public Collection<Product> findAll(Product object, Pagination pagination) {
        return this.productDAO.findAll(object, pagination);
    }
}
