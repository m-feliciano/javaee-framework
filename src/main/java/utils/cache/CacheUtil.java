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
		throw new AssertException("This class must not be instantiated.");
	}

	public static void initProduct(List<Product> list) {
		logger.info("Product cache initialized.");
		products = new ArrayList<>(list);
	}

	public static void initCategory(List<Category> list) {
		logger.info("Category cache initialized.");
		categories = new ArrayList<>(list);
	}

	public static List<Product> getProductsFromCache() {
		logger.info("Getting products from cache.");
		return !products.isEmpty() ? Collections.unmodifiableList(products) : null;
	}

	public static List<Category> getCategoriesFromCache() {
		logger.info("Getting categories from cache.");
		return !categories.isEmpty() ? Collections.unmodifiableList(categories) : null;
	}

	public static void invalidateProduct() {
		logger.info("Invalidating the product cache.");
		products.clear();
	}

	public static void invalidateCategory() {
		logger.info("Invalidating the category cache.");
		categories.clear();
	}

	public static boolean isValidProduct() {
		boolean valid = !products.isEmpty();
		logger.info("Checking product cache validity... valid: {}", valid);
		return valid;
	}

	public static boolean isValidCategory() {
		boolean valid = !categories.isEmpty();
		logger.info("Checking categories cache validity... valid: {}", valid);
		return valid;
	}

}
