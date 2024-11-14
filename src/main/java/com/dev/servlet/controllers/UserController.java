package com.dev.servlet.controllers;

import com.dev.servlet.dao.UserDAO;
import com.dev.servlet.pojo.User;

import javax.inject.Inject;
import java.util.Collection;

public final class UserController extends BaseController<User, Long> {

    public UserDAO userDAO;

    public UserController() {
        // Empty constructor
    }

    @Inject
    public UserController(UserDAO userDAO) {
        super(userDAO);
        this.userDAO = userDAO;
    }

    @Override
    public Collection<User> findAll(User user) {
        return this.userDAO.findAll(user);
    }

    public boolean isEmailAlreadyInUse(String email, Long id) {
        return this.userDAO.isEmailAlreadyInUse(email, id);
    }
}
