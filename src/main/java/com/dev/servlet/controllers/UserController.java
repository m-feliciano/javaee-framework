package com.dev.servlet.controllers;

import com.dev.servlet.dao.UserDAO;
import com.dev.servlet.interfaces.IController;
import com.dev.servlet.pojo.User;

import javax.inject.Inject;
import java.util.List;

public final class UserController implements IController<User, Long> {
    private UserDAO userDAO;

    public UserController() {
        // Empty constructor
    }

    @Inject
    public void setDependencies(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User find(User login) {
        return userDAO.find(login);
    }

//    @Override
//    public User findById(Long id) {
//        return this.userDAO.findById(id);
//    }

    @Override
    public List<User> findAll(User user) {
        return this.userDAO.findAll(user);
    }

    @Override
    public void save(User object) {
        this.userDAO.save(object);
    }

    @Override
    public User update(User object) {
        return this.userDAO.update(object);
    }

    @Override
    public void delete(User object) {
        this.userDAO.delete(object);
    }

}
