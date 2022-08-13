package utils.cache;

import com.mchange.util.AssertException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CacheUtil {
    private static final Logger logger = LoggerFactory.getLogger(CacheUtil.class);
    private static final Map<Map<String, String>, List<?>> SYNCHRONIZED_CACHE = Collections.synchronizedMap(new HashMap<>());

    private CacheUtil() {
        logger.error("CacheUtil is a utility class and should not be instantiated.");
        throw new AssertException("CacheUtil is a utility class and should not be instantiated");
    }

    public static void initCache(String key, String userKey, List<?> list) {
        logger.info("User: {} : Initializing {} cache", userKey, key);
        Map<String, String> map = getMapKey(key, userKey);
        if (SYNCHRONIZED_CACHE.containsKey(map)) {
            logger.info("User: {} : Cache already initialized", userKey);
            return;
        }

        if (list == null) {
            logger.info("User: {} : The list {} is null", userKey, key);
        }
        SYNCHRONIZED_CACHE.put(map, list);
    }

    public static List<?> getFromCache(String key, String userKey) {
        logger.info("User: {} : Retrieving {} from cache", userKey, key);
        Map<String, String> map = getMapKey(key, userKey);
        if (!SYNCHRONIZED_CACHE.containsKey(map)) {
            logger.error("User: {} : Cache not initialized", userKey);
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(SYNCHRONIZED_CACHE.get(map));
    }

    @NotNull
    private static Map<String, String> getMapKey(String key, String userKey) {
        Map<String, String> map = new HashMap<>();
        map.put(key, userKey);
        return map;
    }

    public static void clearCache(String key, String userKey) {
        logger.info("User: {} : Cache {} invalidated.", userKey, key);
        Map<String, String> map = getMapKey(key, userKey);
        if (SYNCHRONIZED_CACHE.containsKey(map)) {
            SYNCHRONIZED_CACHE.remove(map);
        } else {
            logger.error("User: {} : Cache {} not found.", userKey, key);
            throw new AssertException("Cache not found");
        }
    }

}
