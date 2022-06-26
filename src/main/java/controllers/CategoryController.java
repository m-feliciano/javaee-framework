package controllers;

import dao.CategoryDao;
import domain.Category;
import utils.cache.CacheUtil;

import javax.persistence.EntityManager;
import java.util.List;

public class CategoryController {

    private final CategoryDao categoryDao;

    public CategoryController(EntityManager em) {
        this.categoryDao = new CategoryDao(em);
    }

    public void save(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("The category must not be null.");
        }

        this.categoryDao.save(category);
        CacheUtil.invalidateCategory();
    }

    public void update(Category category) {
        this.categoryDao.update(category);
        CacheUtil.invalidateCategory();
    }

    public void delete(Long id) {
        this.categoryDao.delete(id);
        CacheUtil.invalidateCategory();
    }

    /**
     * Find by id.
     *
     * @param id the id
     * @return the category or null if not found
     */

    public Category findById(Long id) {
        if (CacheUtil.isValidCategory()) {
            return this.findAll().stream()
                    .filter(p -> p.getId().equals(id))
                    .findAny()
                    .orElse(null);
        }

        return this.categoryDao.findById(id);
    }

    /**
     * Find all.
     * try get from cache if not found get from db and put in cache.
     *
     * @return the list of categories or empty list if not found
     */

    public List<Category> findAll() {
        if (CacheUtil.isValidCategory()) {
            return CacheUtil.getCategoriesFromCache();
        }

        List<Category> list = this.categoryDao.findAll();
        CacheUtil.initCategory(list);
        return list;
    }

}
