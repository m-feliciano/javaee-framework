package com.dev.servlet.utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public final class JPAUtil {

	private static final EntityManagerFactory factory = Persistence.createEntityManagerFactory("servlets");

	private JPAUtil() {
	}

	/*
	 * This method is used to get an EntityManager.
	 *
	 * @return EntityManager
	 */
	public static EntityManager getEntityManager() {
		return factory.createEntityManager();
	}
}
