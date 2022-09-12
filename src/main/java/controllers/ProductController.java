package controllers;

import dao.ProductDao;
import domain.Product;
import utils.ArrayUtils;
import utils.cache.CacheUtil;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static utils.cache.CacheUtil.getFromCache;

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
        List<Product> products = getFromCache(CACHE_KEY, product.getUser().getLogin());
        if (!ArrayUtils.isArrayNullOrEmpty(products)) {
            Stream<Product> stream = products.stream();

            if (product.getCategory() != null) {
                stream = stream.filter(p -> p.getCategory().getId().equals(product.getCategory().getId()));
            }

            if (product.getName() != null) {
                stream = stream.filter(p -> p.getName().toLowerCase().contains(product.getName().toLowerCase()));
            }

            if (product.getDescription() != null) {
                stream = stream.filter(p -> p.getDescription().toLowerCase().contains(product.getDescription().toLowerCase()));
            }

            return stream.toList();
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
        List<Product> products = getFromCache(CACHE_KEY, product.getUser().getLogin());
        if (!ArrayUtils.isArrayNullOrEmpty(products)) {
            Stream<Product> stream = products.stream();

            Product result = null;
            if (product.getId() != null) {
                result = stream
                        .filter(p -> p.getId().equals(product.getId()))
                        .findFirst()
                        .orElse(null);
            } else {
                if (product.getName() != null) {
                    result = stream
                            .filter(p -> p.getName().toLowerCase().contains(product.getName().toLowerCase()))
                            .findFirst()
                            .orElse(null);
                }

                if (product.getDescription() != null) {
                    result = stream
                            .filter(p -> p.getDescription().toLowerCase().contains(product.getDescription().toLowerCase()))
                            .findAny()
                            .orElse(null);
                }
            }

            if (result != null) {
                return result;
            }
        }

        return this.productDao.find(product);
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
