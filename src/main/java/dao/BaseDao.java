package dao;

import javax.persistence.EntityManager;

public class BaseDao {

    protected final EntityManager em;

    public BaseDao(EntityManager em) {
        this.em = em;
    }

    protected void commitTransaction() {
        this.em.getTransaction().commit();
    }

    protected void beginTransaction() {
        this.em.getTransaction().begin();
    }

    protected void closeTransaction() {
        this.em.close();
    }
}
