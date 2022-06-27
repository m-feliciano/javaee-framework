package dao;

import domain.Category;

import javax.persistence.EntityManager;
import java.util.List;

public class CategoryDao {

    private final EntityManager em;

    public CategoryDao(EntityManager em) {
        this.em = em;
    }

    public Category save(Category category) {
        this.em.getTransaction().begin();
        this.em.persist(category);
        this.em.getTransaction().commit();
        category = this.em.merge(category);
        em.close();
        return category;
    }

    public void update(Category category) {
        this.em.getTransaction().begin();
        this.em.merge(category);
        this.em.getTransaction().commit();
        em.close();
    }

    public boolean delete(Long id) {
        Category category = this.findById(id);
        if (category != null) {
            this.em.getTransaction().begin();
            this.em.remove(category);
            this.em.getTransaction().commit();
            em.close();
            return true;
        }
        return false;
    }

    public Category findById(Long id) {
        return this.em.find(Category.class, id);
    }

    public List<Category> findAll() {
        String jpql = "SELECT c FROM Category c";
        return em.createQuery(jpql, Category.class).getResultList();
    }

}
