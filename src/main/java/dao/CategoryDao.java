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
        beginTransaction();
        this.em.persist(category);
        commitTransaction();
        category = this.em.merge(category);
        this.em.clear();
        closeTransaction();
        return category;
    }

    /**
     * Update.
     *
     * @param category the category
     */
    public void update(Category category) {
        beginTransaction();
        this.em.merge(category);
        commitTransaction();
        closeTransaction();
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
            beginTransaction();
            this.em.remove(category);
            commitTransaction();
            closeTransaction();
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
        Category category = this.em.find(Category.class, id);
        closeTransaction();
        return category;
    }

    /**
     * Find all.
     *
     * @return all categories
     */

    public List<Category> findAll() {
        String jpql = "SELECT c FROM Category c";
        List<Category> list = em.createQuery(jpql, Category.class).getResultList();
        closeTransaction();
        return list;
    }

}
