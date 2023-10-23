package com.dev.servlet.controllers;

import java.util.List;

import javax.persistence.EntityManager;

import com.dev.servlet.dao.UserDAO;
import com.dev.servlet.domain.User;
import com.dev.servlet.interfaces.IController;

public final class UserController implements IController<User, Long> {

	private final UserDAO userDao;

	public UserController(EntityManager em) {
		this.userDao = new UserDAO(em);
	}

	@Override
	public User findById(Long id) {
		return this.userDao.findById(id);
	}

	@Override
	public void save(User object) {
		this.userDao.save(object);
	}

	@Override
	public void update(User object) {
		this.userDao.update(object);
	}

	@Override
	public void delete(User object) {
		this.userDao.delete(object);
	}

	@Override
	public List<User> findAll(User user) {
		return this.userDao.findAll(user);
	}

	public User find(User login) {
		return userDao.find(login);
	}

	public User findByLogin(User user) {
		return userDao.findByLogin(user);
	}

}
