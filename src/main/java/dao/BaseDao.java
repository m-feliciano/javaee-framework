package dao;

import javax.persistence.EntityManager;

public class BaseDao {

    protected final EntityManager em;

    public BaseDao(EntityManager em) {
        this.em = em;
    }

    protected EntityManager getEm() {
        return em;
    }

    protected void close() {
        this.em.close();
    }

    protected void commit() {
        this.em.getTransaction().commit();
    }

    protected void begin() {
        this.em.getTransaction().begin();
    }
}
