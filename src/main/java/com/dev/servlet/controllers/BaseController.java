package com.dev.servlet.controllers;

import com.dev.servlet.dao.BaseDAO;
import com.dev.servlet.interfaces.IController;

import java.util.Collection;

public abstract class BaseController<T, J> implements IController<T, J> {

    protected BaseDAO<T, J> baseDAO;

    public BaseController() {
        // Empty constructor
    }

    public BaseController(BaseDAO<T, J> baseDAO) {
        this.baseDAO = baseDAO;
    }

    @Override
    public T find(T object) {
        return baseDAO.find(object);
    }

    public T findById(J id) {
        return baseDAO.findById(id);
    }

    @Override
    public Collection<T> findAll(T object) {
        return baseDAO.findAll();
    }

    @Override
    public void save(T object) {
        baseDAO.save(object);
    }

    @Override
    public T update(T object) {
        return baseDAO.update(object);
    }

    @Override
    public void delete(T object) {
        baseDAO.delete(object);
    }
}
