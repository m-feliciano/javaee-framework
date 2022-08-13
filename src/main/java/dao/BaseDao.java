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
        if (!this.em.getTransaction().isActive()) {
            this.em.getTransaction().begin();
        }
        this.em.getTransaction().commit();
        this.em.close();
    }

    protected void begin() {
        if (!this.em.getTransaction().isActive()) {
            this.em.getTransaction().begin();
        }
    }
}
