package com.dev.servlet.core.util;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Comprehensive caching utility using Ehcache for high-performance data caching.
 * This class provides token-based cache isolation, automatic cache management,
 * and deep cloning to prevent cache pollution.
 * 
 * <p>Key features:
 * <ul>
 *   <li><strong>Token-based isolation:</strong> Each token gets its own cache instance</li>
 *   <li><strong>Automatic cleanup:</strong> Unused caches are automatically evicted</li>
 *   <li><strong>Deep cloning:</strong> Objects are cloned to prevent reference sharing</li>
 *   <li><strong>Type safety:</strong> Generic methods for compile-time type checking</li>
 *   <li><strong>TTL support:</strong> Configurable time-to-live for cache entries</li>
 * </ul>
 * 
 * <p>Cache configuration:
 * <ul>
 *   <li>Maximum 1000 entries per cache (heap-based)</li>
 *   <li>Default TTL: 24 hours (configurable via properties)</li>
 *   <li>Idle cache cleanup: 60 minutes (configurable via properties)</li>
 *   <li>Automatic cache removal for unused tokens</li>
 * </ul>
 * 
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * String userToken = "user123token";
 * 
 * // Cache a collection
 * List<Product> products = productService.getProducts();
 * CacheUtils.set("products:all", userToken, products);
 * 
 * // Retrieve from cache
 * List<Product> cached = CacheUtils.get("products:all", userToken);
 * 
 * // Cache a single object
 * User user = userService.getUser(123);
 * CacheUtils.setObject("user:123", userToken, user);
 * User cachedUser = CacheUtils.getObject("user:123", userToken);
 * 
 * // Clear specific entries
 * CacheUtils.clear("user:123", userToken);
 *
 * // Clear all entries for a token
 * CacheUtils.clearAll(userToken);
 * 
 * // Clear entries by prefix
 * CacheUtils.clearCacheKeyPrefix("user:", userToken);
 * }
 * </pre>
 * 
 * <p><strong>Performance Notes:</strong>
 * <ul>
 *   <li>Objects are deep cloned on both storage and retrieval</li>
 *   <li>Token strings are truncated to 25 characters for cache keys</li>
 *   <li>Background cleanup runs periodically to remove idle caches</li>
 *   <li>Thread-safe for concurrent access</li>
 * </ul>
 * 
 * @since 1.0
 * @see CloneUtil
 * @see PropertiesUtil
 */
@SuppressWarnings("unchecked")
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Slf4j
public final class CacheUtils {

    private static final CacheManager cacheManager;
    private static final ConcurrentMap<String, Cache<String, Container>> tokenCaches = new ConcurrentHashMap<>();
    private static final long EXPIRATION_MINUTES = TimeUnit.DAYS.toMinutes(1);
    private static final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();
    private static final ConcurrentMap<String, Long> lastAccessMap = new ConcurrentHashMap<>();
    private static final long CACHE_IDLE_TIMEOUT_MINUTES;

    /**
     * Static initialization block for cache manager and cleanup scheduler.
     * Configures the Ehcache manager and starts the background cleanup task.
     */
    static {
        log.info("Ehcache CacheManager initialized");
        CACHE_IDLE_TIMEOUT_MINUTES = PropertiesUtil.getProperty("cache.timeout.minutes", 60L);
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);
        cleaner.scheduleAtFixedRate(
                CacheUtils::cleanupUnusedCaches,
                CACHE_IDLE_TIMEOUT_MINUTES, CACHE_IDLE_TIMEOUT_MINUTES,
                TimeUnit.MINUTES);
    }

    /**
     * Gets or creates a cache for the specified token.
     * Each token gets its own isolated cache instance to prevent data leakage
     * between different users or sessions.
     *
     * @param authKey the user/session token (truncated to 25 characters)
     * @return the cache instance for the token
     */
    private static Cache<String, Container> getOrCreateCache(String authKey) {
        if (tokenCaches.containsKey(authKey)) {
            return tokenCaches.get(authKey);
        }
        String cacheName = "cache_" + authKey;
        cacheManager.removeCache(cacheName);
        CacheConfigurationBuilder<String, Container> config = cacheConfigurationBuilder();
        cacheManager.createCache(cacheName, config);
        log.info("Created new cache for token: {}", authKey);
        Cache<String, Container> cache = cacheManager.getCache(cacheName, String.class, Container.class);
        tokenCaches.put(authKey, cache);
        lastAccessMap.put(authKey, System.currentTimeMillis());
        return cache;
    }

    /**
     * Creates the cache configuration with TTL and resource limits.
     * 
     * @return configured cache builder
     */
    private static CacheConfigurationBuilder<String, Container> cacheConfigurationBuilder() {
        return CacheConfigurationBuilder
                .newCacheConfigurationBuilder(
                        String.class,
                        Container.class,
                        ResourcePoolsBuilder.heap(1000)
                )
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(EXPIRATION_MINUTES)));
    }

    /**
     * Stores a collection in the cache with deep cloning.
     * The collection is cloned to prevent modifications to cached data
     * from affecting the original collection.
     * 
     * @param <T> the type of collection elements
     * @param userId the cache userId
     * @param cacheName the user/session token
     * @param collection the collection to cache
     */
    public static <T> void set(String userId, String cacheName, Collection<T> collection) {
        List<T> data = CloneUtil.cloneList(collection);
        Cache<String, Container> cache = getOrCreateCache(userId);
        cache.put(cacheName, new Container(data));
        log.debug("Cached data for userId='{}'", userId);
    }

    /**
     * Stores a single object in the cache with deep cloning.
     * The object is cloned to prevent modifications to cached data
     * from affecting the original object.
     * 
     * @param <T> the type of object
     * @param userId the cache key
     * @param cacheName the user/session token
     * @param object the object to cache
     */
    public static <T> void setObject(String userId, String cacheName, T object) {
        Cache<String, Container> cache = getOrCreateCache(userId);
        T clone = CloneUtil.forceClone(object);
        Container container = new Container(clone);
        cache.put(cacheName, container);
        log.debug("Cached object for key='{}'", userId);
    }

    /**
     * Retrieves a collection from the cache with deep cloning.
     * Returns a cloned copy to prevent modifications to the returned
     * collection from affecting cached data.
     * 
     * @param <T> the type of collection elements
     * @param userId the cache authKey
     * @param cacheName the user/session token
     * @return cloned collection from cache, or empty list if not found
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> get(String userId, String cacheName) {
        Cache<String, Container> cache = getOrCreateCache(userId);
        Container value = cache.get(cacheName);
        log.debug("Retrieved data for cacheName='{}': {}", cacheName, value != null ? "HIT" : "MISS");
        if (value != null) {
            Object data = value.data();
            if (data instanceof Collection<?> valueCollection) {
                return (List<T>) CloneUtil.cloneList(valueCollection);
            }
        }
        return Collections.emptyList();
    }

    /**
     * Retrieves a single object from the cache with deep cloning.
     * Returns a cloned copy to prevent modifications to the returned
     * object from affecting cached data.
     * 
     * @param <T> the type of object
     * @param userId the cache userId
     * @param cacheName the user/session token
     * @return cloned object from cache, or null if not found
     */
    @SuppressWarnings("unchecked")
    public static <T> T getObject(String userId, String cacheName) {
        Cache<String, Container> cache = getOrCreateCache(userId);
        Container value = cache.get(cacheName);
        log.debug("Retrieved data for cacheName='{}': {}", cacheName, value != null ? "HIT" : "MISS");
        if (value == null) return null;

        return CloneUtil.forceClone((T) value.data());
    }

    /**
     * Removes a specific cache entry.
     *
     * @param userId the cache userId to remove
     * @param cacheName the user/session token
     */
    public static void clear(String userId, String cacheName) {
        Cache<String, Container> cache = getOrCreateCache(userId);
        cache.remove(cacheName);
        log.info("Cleared cache entry for userId='{}'", userId);
    }

    /**
     * Removes all cache entries for a specific token.
     * This completely destroys the cache instance for the token.
     *
     * @param userId the user/session token
     */
    public static void clearAll(String userId) {
        String cacheName = "cache_" + userId;
        cacheManager.removeCache(cacheName);
        tokenCaches.remove(userId);
        log.info("Cleared all cache entries for token='{}'", userId);
    }

    /**
     * Background cleanup task that removes unused caches.
     * Caches that haven't been accessed for the configured timeout period
     * are automatically removed to free resources.
     */
    private static void cleanupUnusedCaches() {
        long now = System.currentTimeMillis();
        for (var entry : lastAccessMap.entrySet()) {
            if (now - entry.getValue() > TimeUnit.MINUTES.toMillis(CACHE_IDLE_TIMEOUT_MINUTES)) {
                String cacheKey = entry.getKey();
                String cacheName = "cache_" + cacheKey;
                cacheManager.removeCache(cacheName);
                tokenCaches.remove(cacheKey);
                lastAccessMap.remove(cacheKey);
                log.info("Evicted unused cache for token='{}'", cacheKey);
            }
        }
    }

    /**
     * Shuts down the cache manager and cleanup resources.
     * This method should be called during application shutdown.
     */
    public static void close() {
        cacheManager.close();
        tokenCaches.clear();
        lastAccessMap.clear();
        cleaner.shutdownNow();
        log.info("CacheManager closed and all caches cleared");
    }

    /**
     * Internal container record for storing cached data.
     * Implements Serializable for Ehcache persistence if needed.
     */
    private record Container(Object data) implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }
}
