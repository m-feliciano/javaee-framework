package com.dev.servlet.controllers;

import com.dev.servlet.dao.CategoryDAO;
import com.dev.servlet.pojo.Category;

import javax.inject.Inject;
import java.util.Collection;

public final class CategoryController extends BaseController<Category, Long> {

    public CategoryDAO categoryDAO;

    public CategoryController() {
        // Empty constructor
    }

    @Inject
    public CategoryController(CategoryDAO categoryDAO) {
        super(categoryDAO);
        this.categoryDAO = categoryDAO;
    }

    @Override
    public Collection<Category> findAll(Category category) {
        return this.categoryDAO.findAll(category);
    }

}
