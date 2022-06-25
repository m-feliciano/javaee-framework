package dao;

import domain.Category;

import javax.persistence.EntityManager;
import java.util.List;

public class CategoryDao {

    private final EntityManager em;

    public CategoryDao(EntityManager em) {
        this.em = em;
    }

    public void save(Category category) {
        this.em.persist(category);
    }

    public void update(Category category) {
        this.em.merge(category);
    }

    public boolean delete(Long id) {
        Category category = this.findById(id);
        if (category != null) {
            this.em.remove(category);
            return true;
        }
        return false;
    }

    public Category findById(Long id) {
        return this.em.find(Category.class, id);
    }

    public List<Category> findAll() {
        String jpsl = "SELECT c FROM Category c";
        return em.createQuery(jpsl, Category.class).getResultList();
    }

}
