package com.dev.servlet.controllers;

import com.dev.servlet.dao.CategoryDAO;
import com.dev.servlet.domain.Category;
import com.dev.servlet.interfaces.IController;
import com.dev.servlet.utils.CacheUtil;

import javax.persistence.EntityManager;
import java.util.List;

public final class CategoryController implements IController<Category, Long> {

    private static final String CACHE_KEY = "categories";

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
        CacheUtil.clear(CACHE_KEY, category.getUser().getToken());
    }

    @Override
    public void update(Category category) {
        this.categoryDao.update(category);
        CacheUtil.clear(CACHE_KEY, category.getUser().getToken());
    }

    @Override
    public void delete(Category category) {
        this.categoryDao.delete(category);
        CacheUtil.clear(CACHE_KEY, category.getUser().getToken());
    }

}
