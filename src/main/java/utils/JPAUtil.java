package utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtil {

	private static final EntityManagerFactory FACTORY = Persistence.createEntityManagerFactory("servlet");

	private JPAUtil() {
		throw new IllegalArgumentException("This class cannot be instantiated.");
	}

	public static EntityManager getEntityManager() {
		return FACTORY.createEntityManager();
	}
}
