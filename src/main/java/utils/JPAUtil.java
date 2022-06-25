package utils;

import com.mchange.util.AssertException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtil {

    /*
     * This method is used to get an EntityManagerFactory.
     *
     * @return EntityManagerFactory
     */

    private static final EntityManagerFactory FACTORY = Persistence.createEntityManagerFactory("servlet");

    private JPAUtil() {
        throw new AssertException("JPAUtil is a utility class and should not be instantiated");
    }

    /**
     * This method is used to get an entity manager.
     *
     * @return EntityManager
     */

    public static EntityManager getEntityManager() {
        return FACTORY.createEntityManager();
    }
}
