package com.dev.servlet.dao;

import java.util.List;

import javax.persistence.EntityManager;

import com.dev.servlet.domain.User;
import com.dev.servlet.domain.enums.Status;

public class UserDAO extends BaseDAO<User, Long> {

	public static final String LOGIN = "login";
	public static final String PASSWORD = "password";
	public static final String STATUS = "status";

	public UserDAO(EntityManager em) {
		super(em, User.class);
	}

	/**
	 * Find by login.
	 *
	 * @param user the user and password
	 * @return the user found or null if not found
	 */
	public User findByLogin(User user) {
		String jpql = """
				 SELECT u FROM User u
				 WHERE u.login = :login
				 AND u.status = :status
				 AND u.password = :password
				""";

		User obj = em.createQuery(jpql, User.class).setParameter(LOGIN, user.getLogin().toLowerCase())
				.setParameter(PASSWORD, user.getPassword()).setParameter(STATUS, Status.ACTIVE.getDescription())
				.getResultList().stream().findFirst().orElse(null);

		return obj;
	}

	/**
	 * Find by login.
	 *
	 * @param user the user
	 * @return the user found or null if not found
	 */
	public User find(User user) {
		String jpql = "SELECT u FROM User u JOIN FETCH u.perfis p WHERE lower(u.login) = :login";

		return em.createQuery(jpql, User.class).setParameter(LOGIN, user.getLogin().toLowerCase()).getResultList()
				.stream().findFirst().orElse(null);
	}

	@Override
	public List<User> findAll(User object) {
		// TODO Auto-generated method stub
		return null;
	}

	public void delete(User object) {
		// TODO Auto-generated method stub

	}
}
