package controllers;

import dao.ProductDao;
import domain.Category;
import domain.Product;
import utils.cache.CacheUtil;

import javax.persistence.EntityManager;
import java.util.List;

public class ProductController {

    private final ProductDao productDao;

    public ProductController(EntityManager em) {
        this.productDao = new ProductDao(em);
    }


    /**
     * Save.
     * throws IllegalArgumentException if product is null
     *
     * @param product the product
     */

    public void save(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("The product must not be null.");
        }
        this.productDao.save(product);
        CacheUtil.invalidateProduct();
    }

    /**
     * Update.
     * invalidate cache if found.
     *
     * @param prod the prod
     */

    public void update(Product prod) {
        this.productDao.update(prod);
        CacheUtil.invalidateProduct();
    }

    public void delete(Long id) {
        this.productDao.delete(id);
        CacheUtil.invalidateProduct();
    }

    /**
     * Find all.
     * try get from cache if not found get from db and put in cache.
     *
     * @return the list of products or empty list if not found
     */
    public List<Product> findAll() {
        if (CacheUtil.isValidProduct()) {
            return CacheUtil.getProductsFromCache();
        }

        List<Product> list = this.productDao.findAll();
        CacheUtil.initProduct(list);
        return list;
    }

    /**
     * Find by id.
     * try get from cache if not found get from db and put in cache.
     *
     * @param id the id
     * @return the product or null if not found
     */

    public Product findById(Long id) {
        if (CacheUtil.isValidProduct()) {
            return findAll().stream()
                    .filter(p -> p.getId().equals(id))
                    .findAny()
                    .orElse(null);
        }

        return this.productDao.findById(id);
    }

    /**
     * Find by catwgory name.
     * try get from cache if not found get from db and put in cache.
     *
     * @param name the name
     * @return the list of products or empty list if not found
     */

    public List<Product> findAllByName(String name) {
        if (CacheUtil.isValidProduct()) {
            return findAll().stream()
                    .filter(prod -> prod.getName().toLowerCase().contains(name.toLowerCase()))
                    .toList();
        }

        return productDao.findAllByName(name);
    }


    /**
     * Find by category name.
     *
     * @param name the name
     * @return the list of products or empty list if not found
     */

    public List<Product> findAllByCategoryName(String name) {
        if (CacheUtil.isValidProduct()) {
            return findAll().stream()
                    .filter(prod -> prod.getCategories().stream()
                            .map(Category::getName)
                            .anyMatch(p -> p.equalsIgnoreCase(name)))
                    .toList();
        }
        return productDao.findAllByCategoryName(name);
    }

}
