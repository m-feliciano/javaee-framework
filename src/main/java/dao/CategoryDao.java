package dao;

import domain.Category;

import javax.persistence.EntityManager;
import java.util.List;

public class CategoryDao extends BaseDao {
    public CategoryDao(EntityManager em) {
        super(em);
    }

    /**
     * Save.
     *
     * @param category the category
     * @return the category saved
     */
    public Category save(Category category) {
        begin();
        this.em.persist(category);
        commit();
        category = this.em.merge(category);
        close();
        return category;
    }

    /**
     * Update.
     *
     * @param category the category
     */
    public void update(Category category) {
        begin();
        this.em.merge(category);
        commit();
        close();
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
            begin();
            this.em.remove(category);
            commit();
            close();
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
        List<Category> categories = em.createQuery(jpql, Category.class).getResultList();
        close();
        return categories;
    }

}
