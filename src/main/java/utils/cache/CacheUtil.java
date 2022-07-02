package utils.cache;

import com.mchange.util.AssertException;
import domain.Category;
import domain.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CacheUtil {

    private static final Logger logger = LoggerFactory.getLogger(CacheUtil.class);

    private static List<Product> products = new ArrayList<>();
    private static List<Category> categories = new ArrayList<>();

    private CacheUtil() {
        logger.error("CacheUtil is a utility class and should not be instantiated.");
        throw new AssertException("CacheUtil is a utility class and should not be instantiated");
    }

    public static void initProduct(List<Product> list) {
        logger.info("Initializing product cache");
        products = new ArrayList<>(list);
    }

    public static void initCategory(List<Category> list) {
        logger.info("Initializing category cache");
        categories = new ArrayList<>(list);
    }

    public static List<Product> getProductsFromCache() {
        logger.info("{}", products.isEmpty() ? "Product cache is empty" : "Retrieving products from cache");
        return Collections.unmodifiableList(products);
    }

    public static List<Category> getCategoriesFromCache() {
       logger.info("{}", products.isEmpty() ? "Category cache is empty" : "Retrieving categories from cache");
        return Collections.unmodifiableList(categories);
    }

    public static void clearProduct() {
        logger.info("Product cache invalidated.");
        products.clear();
    }

    public static void clearCategory() {
        logger.info("Category cache invalidated.");
        categories.clear();
    }

}
