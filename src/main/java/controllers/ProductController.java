package controllers;

import dao.ProductDao;
import domain.Category;
import domain.Product;
import utils.ArrayUtils;
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
        clearCache(product);
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
        clearCache(prod);
    }

    /**
     * delete by id.
     * invalidate cache after delete.
     *
     * @param product the product
     */

    public void delete(Product product) {
        this.productDao.delete(product);
        clearCache(product);
    }

    /**
     * Find all.
     * try get from cache if not found get from db and put in cache.
     *
     * @return the list of products or empty list if not found
     */
    public List<Product> findAll(Product product) {
        List<Product> products = (List<Product>) CacheUtil.getFromCache(CACHE_KEY, product.getUser().getLogin());
        if (!ArrayUtils.isArrayNullOrEmpty(products)) {

            if (product.getName() != null) {
                products = products.stream().filter(p -> p.getName().toLowerCase().contains(product.getName().toLowerCase())).toList();
            }

            if (product.getDescription() != null) {
                products = products.stream().filter(p -> p.getDescription().toLowerCase().contains(product.getDescription().toLowerCase())).toList();
            }

            return products;
        }

        products = this.productDao.findAll(product);
        CacheUtil.initCache(CACHE_KEY, product.getUser().getLogin(), products);
        return products;
    }

    /**
     * Find by id.
     * try get from cache if not found get from db and put in cache.
     *
     * @param product the product
     * @return the product or null if not found
     */

    public Product find(Product product) {
        List<Product> products = (List<Product>) CacheUtil.getFromCache(CACHE_KEY, product.getUser().getLogin());
        if (!ArrayUtils.isArrayNullOrEmpty(products)) {
            return products.stream().filter(p -> p.getId().equals(product.getId())).findAny().orElse(null);
        }

        return this.productDao.find(product);
    }

    /**
     * Find all by category.
     *
     * @param category the category
     * @return the list of products or empty list if not found
     */

    public List<Product> findAllByCategory(Category category) {
        List<Product> products = findAll(new Product());
        if (!ArrayUtils.isArrayNullOrEmpty(products)) {
            return products.stream().filter(prod -> prod.getCategories().stream().map(Category::getName).anyMatch(p -> p.equalsIgnoreCase(category.getName()))).toList();
        }

        return productDao.findAllByCategory(category);
    }

    /**
     * Clear cache.
     *
     * @param product the product
     */
    private void clearCache(Product product) {
        CacheUtil.clearCache(CACHE_KEY, product.getUser().getLogin());
    }

}
