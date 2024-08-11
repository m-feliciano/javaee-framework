package com.dev.servlet.controllers;

import com.dev.servlet.dao.CategoryDAO;
import com.dev.servlet.domain.Category;
import com.dev.servlet.interfaces.IController;

import javax.persistence.EntityManager;
import java.util.List;

public final class CategoryController implements IController<Category, Long> {

    private final CategoryDAO categoryDao;

    public CategoryController(EntityManager em) {
        this.categoryDao = new CategoryDAO(em);
    }

    @Override
    public Category findById(Long id) {
        return this.categoryDao.findById(id);
    }

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
