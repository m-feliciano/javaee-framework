package utils.cache;

import com.mchange.util.AssertException;
import com.mchange.v1.util.ArrayUtils;
import domain.Category;
import domain.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.*;

public final class CacheUtil {
    private static final Logger logger = LoggerFactory.getLogger(CacheUtil.class);
    private static Map<String, List<?>> cache = Collections.synchronizedMap(new HashMap<>());

    private CacheUtil() {
        logger.error("CacheUtil is a utility class and should not be instantiated.");
        throw new AssertException("CacheUtil is a utility class and should not be instantiated");
    }

    public static void initCache(String key, List<?> list) {
        logger.info("Initializing {} cache", key);
        if (cache.containsKey(key)) {
            logger.info("Cache already initialized");
            return;
        }

        if (list == null) {
            logger.info("List {} is null", key);
        }
        cache.put(key, list);
    }

    public static List<?> getFromCache(String key) {
        logger.info("Retrieving {} from cache", key);

        if (!cache.containsKey(key)) {
            logger.error("Cache not initialized");
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(cache.get(key));
    }

    public static void clearCache(String key) {
        logger.info("cache {} invalidated.", key);

        if (cache.containsKey(key)) {
            cache.remove(key);
        } else {
            logger.error("Cache {} not found.", key);
            throw new AssertException("Cache not found");
        }
    }

}
