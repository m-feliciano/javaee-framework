package com.dev.servlet.controllers;

import com.dev.servlet.dao.CategoryDAO;
import com.dev.servlet.interfaces.IController;
import com.dev.servlet.pojo.Category;

import javax.inject.Inject;
import java.util.List;

public final class CategoryController implements IController<Category, Long> {
    private CategoryDAO categoryDao;

    public CategoryController() {
        // Empty constructor
    }

    @Inject
    public void setDependencies(CategoryDAO categoryDao) {
        this.categoryDao = categoryDao;
    }

//    @Override
//    public Category findById(Long id) {
//        return this.categoryDao.findById(id);
//    }

    @Override
    public Category find(Category object) {
        return this.categoryDao.find(object);
    }

    @Override
    public List<Category> findAll(Category category) {
        return this.categoryDao.findAll(category);
    }

    @Override
    public void save(Category category) {
        this.categoryDao.save(category);
    }

    @Override
    public Category update(Category category) {
        return this.categoryDao.update(category);
    }

    @Override
    public void delete(Category category) {
        this.categoryDao.delete(category);
    }

}
