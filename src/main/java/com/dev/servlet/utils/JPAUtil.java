package com.dev.servlet.utils;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@ApplicationScoped
public final class JPAUtil {

    private static final EntityManagerFactory factory = Persistence.createEntityManagerFactory("servlets");

    private JPAUtil() {
    }

    /*
     * This method is used to get an EntityManager.
     *
     * @return EntityManager
     */
    @Produces
    @RequestScoped
    public static EntityManager getEntityManager() {
        return factory.createEntityManager();
    }

    /**
     * This method is used to close the EntityManagerFactory.
     */
    public static void closeEntityManagerFactory() {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
    }


    /**
     * This method is used to close the EntityManager instance.
     *
     * @param em
     */
    public void close(@Disposes EntityManager em) {
        em.close();
    }
}
