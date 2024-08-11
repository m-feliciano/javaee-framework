package com.dev.servlet.controllers;

import com.dev.servlet.dao.UserDAO;
import com.dev.servlet.domain.User;
import com.dev.servlet.interfaces.IController;
import com.dev.servlet.utils.CacheUtil;

import javax.persistence.EntityManager;
import java.util.List;

public final class UserController implements IController<User, Long> {

    private static final String CACHE_KEY = "users";

    private final UserDAO userDao;

    public UserController(EntityManager em) {
        this.userDao = new UserDAO(em);
    }

    public User find(User login) {
        return userDao.find(login);
    }

    @Override
    public User findById(Long id) {
        return this.userDao.findById(id);
    }

    @Override
    public List<User> findAll(User user) {
        return this.userDao.findAll(user);
    }

    @Override
    public void save(User object) {
        this.userDao.save(object);
        CacheUtil.clear(CACHE_KEY, object.getToken());
    }

    @Override
    public void update(User object) {
        this.userDao.update(object);
        CacheUtil.clear(CACHE_KEY, object.getToken());
    }

    @Override
    public void delete(User object) {
        this.userDao.delete(object);
        CacheUtil.clear(CACHE_KEY, object.getToken());
    }

}
