package controllers;

import dao.CategoryDao;
import domain.Category;
import utils.cache.CacheUtil;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

public class CategoryController {

    private final CategoryDao categoryDao;

    public CategoryController(EntityManager em) {
        this.categoryDao = new CategoryDao(em);
    }

    /**
     * Save.
     * throws IllegalArgumentException if category is null
     * clear cache after saved.
     *
     * @param category the category
     */

    public Category save(Category category) {
        if (Objects.isNull(category)) throw new IllegalArgumentException("The category must not be null.");
        CacheUtil.clearCategory();
        return this.categoryDao.save(category);
    }

    /**
     * Update.
     * clear cache after update.
     *
     * @param category the category
     */


    public void update(Category category) {
        this.categoryDao.update(category);
        CacheUtil.clearCategory();
    }

    /**
     * delete by id.
     * clear cache after delete.
     *
     * @param id the id
     */

    public void delete(Long id) {
        this.categoryDao.delete(id);
        CacheUtil.clearCategory();
    }

    /**
     * Find by id.
     *
     * @param id the id
     * @return the category or null if not found
     */

    public Category findById(Long id) {
        List<Category> categories = CacheUtil.getCategoriesFromCache();
        if (!categories.isEmpty()) {
            return categories.stream()
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
        List<Category> categories = CacheUtil.getCategoriesFromCache();
        if (!categories.isEmpty()) {
            return categories;
        }

        categories = this.categoryDao.findAll();
        CacheUtil.initCategory(categories);
        return categories;
    }

}
