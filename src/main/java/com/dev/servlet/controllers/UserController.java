package com.dev.servlet.controllers;

import com.dev.servlet.dao.UserDAO;
import com.dev.servlet.domain.User;
import com.dev.servlet.interfaces.IController;

import javax.persistence.EntityManager;
import java.util.List;

public final class UserController implements IController<User, Long> {

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
    }

    @Override
    public User update(User object) {
        return this.userDao.update(object);
    }

    @Override
    public void delete(User object) {
        this.userDao.delete(object);
    }

}
