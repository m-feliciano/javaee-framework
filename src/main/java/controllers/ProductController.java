package controllers;

import dao.ProductDao;
import domain.Category;
import domain.Product;
import utils.cache.CacheUtil;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

public class ProductController {

    private final ProductDao productDao;
    private static final String CACHE_KEY = "products";

    public ProductController(EntityManager em) {
        this.productDao = new ProductDao(em);
    }


    /**
     * Save.
     * throws IllegalArgumentException if product is null
     *
     * @param product the product
     */

    public Product save(Product product) {
        if (Objects.isNull(product)) throw new IllegalArgumentException("The product must not be null.");
        CacheUtil.clearCache(CACHE_KEY);
        return this.productDao.save(product);
    }

    /**
     * Update.
     * invalidate cache after update.
     *
     * @param prod the prod
     */

    public void update(Product prod) {
        this.productDao.update(prod);
        CacheUtil.clearCache(CACHE_KEY);
    }

    /**
     * delete by id.
     * invalidate cache after delete.
     *
     * @param id the id
     */

    public void delete(Long id) {
        this.productDao.delete(id);
        CacheUtil.clearCache(CACHE_KEY);
    }

    /**
     * Find all.
     * try get from cache if not found get from db and put in cache.
     *
     * @return the list of products or empty list if not found
     */
    public List<Product> findAll() {
        List<Product> products = (List<Product>) CacheUtil.getFromCache(CACHE_KEY);
        if (!products.isEmpty()) return products;
        products = this.productDao.findAll();
        CacheUtil.initCache(CACHE_KEY, products);
        return products;
    }

    /**
     * Find by id.
     * try get from cache if not found get from db and put in cache.
     *
     * @param id the id
     * @return the product or null if not found
     */

    public Product findById(Long id) {
        List<Product> products = findAll();
        if (!products.isEmpty()) {
            return findAll().stream()
                    .filter(p -> p.getId().equals(id))
                    .findAny()
                    .orElse(null);
        }

        return this.productDao.findById(id);
    }

    /**
     * Find by name.
     * try get from cache if not found get from db and put in cache.
     *
     * @param name the name
     * @return the list of products or empty list if not found
     */

    public List<Product> findAllByName(String name) {
        List<Product> products = findAll();
        if (!products.isEmpty()) {
            return findAll().stream()
                    .filter(prod -> prod.getName().toLowerCase().contains(name.toLowerCase()))
                    .toList();
        }

        return productDao.findAllByName(name);
    }

    /**
     * Find by description.
     * try get from cache if not found get from db and put in cache.
     *
     * @param description the description
     * @return the list of products or empty list if not found
     */

    public List<Product> findAllByDescription(String description) {
        List<Product> products = findAll();
        if (!products.isEmpty()) {
            return findAll().stream()
                    .filter(prod -> prod.getDescription().toLowerCase().contains(description.toLowerCase()))
                    .toList();
        }

        return productDao.findAllByDescription(description);
    }


    /**
     * Find all by category name.
     *
     * @param name the name
     * @return the list of products or empty list if not found
     */

    public List<Product> findAllByCategoryName(String name) {
        List<Product> products = findAll();
        if (!products.isEmpty()) {
            return products.stream()
                    .filter(prod -> prod.getCategories().stream()
                            .map(Category::getName)
                            .anyMatch(p -> p.equalsIgnoreCase(name)))
                    .toList();
        }

        return productDao.findAllByCategoryName(name);
    }

}
