package controllers;

import dao.UserDao;
import domain.User;

import javax.persistence.EntityManager;

public class UserController {

	private final UserDao userDao;

	public UserController(EntityManager em) {
		this.userDao = new UserDao(em);
	}

	public void save(User user) {
		if (user == null) {
			throw new IllegalArgumentException("The user must not be null.");
		}

		this.userDao.save(user);
	}

	public void update(User prod) {
		this.userDao.update(prod);
	}

	public void delete(Long id) {
		this.userDao.delete(id);
	}

	public User findById(Long id) {
		return this.userDao.findById(id);
	}

	public User findByLogin(String login) {
		return userDao.findByLogin(login);
	}

}
