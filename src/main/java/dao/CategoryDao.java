package dao;

import domain.Category;

import javax.persistence.EntityManager;
import java.util.List;

public class CategoryDao {

    private final EntityManager em;

    public CategoryDao(EntityManager em) {
        this.em = em;
    }

    /**
     * Save.
     *
     * @param category the category
     * @return the category saved
     */
    public Category save(Category category) {
        this.em.getTransaction().begin();
        this.em.persist(category);
        this.em.getTransaction().commit();
        category = this.em.merge(category);
        em.close();
        return category;
    }

    /**
     * Update.
     *
     * @param category the category
     */
    public void update(Category category) {
        this.em.getTransaction().begin();
        this.em.merge(category);
        this.em.getTransaction().commit();
        em.close();
    }

    /**
     * Delete by id.
     *
     * @param id the id
     * @return true if deleted, false if not found
     */

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

    /**
     * Find by id.
     *
     * @param id the id
     * @return the category if found, null if not found
     */

    public Category findById(Long id) {
        return this.em.find(Category.class, id);
    }

    /**
     * Find all.
     *
     * @return all categories
     */

    public List<Category> findAll() {
        String jpql = "SELECT c FROM Category c";
        return em.createQuery(jpql, Category.class).getResultList();
    }

}
