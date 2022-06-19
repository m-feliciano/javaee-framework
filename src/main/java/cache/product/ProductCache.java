package cache.product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mchange.util.AssertException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import entities.Product;

public final class ProductCache {
	private static final Logger logger = LoggerFactory.getLogger(ProductCache.class);

	private static boolean valid = false;

	private static List<Product> products;

	private ProductCache() {
		throw new AssertException("This class must not be instantiated.");
	}

	public static void init(List<Product> list) {
		logger.info("Product cache initialized.");
		products = new ArrayList<>(list);
		valid = true;
	}

	public static List<Product> getProductsFromCache() {
		logger.info("Getting product list from cache.");
		return valid ? Collections.unmodifiableList(products) : null;
	}

	public static void invalidateCache() {
		logger.info("Invalidating the product cache.");
		products.clear();
		valid = false;
	}

	public static boolean isCacheValid() {
		logger.info("Checking product cache validity... valid: " + valid);
		return valid;
	}

}
