package com.dev.servlet.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

public class BaseDAO<T, E> {

    @Inject
    protected EntityManager em;
    private Class<T> entityClass;
    protected static final Logger logger = LoggerFactory.getLogger(BaseDAO.class);

    public BaseDAO() {
    }

    public BaseDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public BaseDAO(Class<T> entityClass, EntityManager em) {
        this.entityClass = entityClass;
        this.em = em;
    }

    public T findById(E id) {
        return em.find(entityClass, id);
    }

    public T save(T object) {
        try {
            this.beginTransaction();
            this.em.persist(object);
            this.commitTransaction();
        } catch (Exception e) {
            logger.error("Error saving object: {}", e.getMessage());
            rollbackTransaction();
        }
        return object;
    }

    public T update(T object) {
        try {
            this.beginTransaction();
            object = em.merge(object);
            this.commitTransaction();
            return object;
        } catch (Exception e) {
            logger.error("Error updating object: {}", e.getMessage());
            rollbackTransaction();
        }
        return null;
    }

    public void delete(T object) {
        this.em.remove(object);
    }

    public T find(T object) {
        return em.find(entityClass, object);
    }

    public List<T> findAll() {
        return em.createQuery("SELECT t FROM " + entityClass.getSimpleName() + " t", entityClass)
                .getResultList();
    }

    public void close() {
        if (em.isOpen()) {
            em.close();
        }
    }

    public void beginTransaction() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    public void commitTransaction() {
        try {
            if (em.getTransaction().isActive()) {
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            logger.error("Error committing transaction: {}", e.getMessage());
            rollbackTransaction();
        }
    }

    public void rollbackTransaction() {
        try {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } catch (Exception e) {
            logger.error("Error rolling back transaction: {}", e.getMessage());
        }
    }

    public void saveWithoutCommit(T object) {
        em.persist(object);
    }

    public void updateWithoutCommit(T object) {
        em.merge(object);
    }

    public void deleteWithoutCommit(T object) {
        em.remove(object);
    }

    public void flush() {
        em.flush();
    }
}
