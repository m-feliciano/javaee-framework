package com.dev.servlet.core.util;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

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

    static {
        log.info("Ehcache CacheManager initialized");
        CACHE_IDLE_TIMEOUT_MINUTES = PropertiesUtil.getProperty("cache.timeout.minutes", 60L);
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);
        cleaner.scheduleAtFixedRate(
                CacheUtils::cleanupUnusedCaches,
                CACHE_IDLE_TIMEOUT_MINUTES, CACHE_IDLE_TIMEOUT_MINUTES,
                TimeUnit.MINUTES);
    }

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

    private static CacheConfigurationBuilder<String, Container> cacheConfigurationBuilder() {
        return CacheConfigurationBuilder
                .newCacheConfigurationBuilder(
                        String.class,
                        Container.class,
                        ResourcePoolsBuilder.heap(1000)
                )
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(EXPIRATION_MINUTES)));
    }

    public static <T> void set(String userId, String cacheName, Collection<T> collection) {
        List<T> data = CloneUtil.cloneList(collection);
        Cache<String, Container> cache = getOrCreateCache(userId);
        cache.put(cacheName, new Container(data));
        log.debug("Cached data for userId='{}'", userId);
    }

    public static <T> void setObject(String userId, String cacheName, T object) {
        Cache<String, Container> cache = getOrCreateCache(userId);
        T clone = CloneUtil.forceClone(object);
        Container container = new Container(clone);
        cache.put(cacheName, container);
        log.debug("Cached object for key='{}'", userId);
    }

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

    @SuppressWarnings("unchecked")
    public static <T> T getObject(String userId, String cacheName) {
        Cache<String, Container> cache = getOrCreateCache(userId);
        Container value = cache.get(cacheName);
        log.debug("Retrieved data for cacheName='{}': {}", cacheName, value != null ? "HIT" : "MISS");
        if (value == null) return null;

        return CloneUtil.forceClone((T) value.data());
    }

    public static void clear(String userId, String cacheName) {
        Cache<String, Container> cache = getOrCreateCache(userId);
        cache.remove(cacheName);
        log.info("Cleared cache entry for userId='{}'", userId);
    }

    public static void clearAll(String userId) {
        String cacheName = "cache_" + userId;
        cacheManager.removeCache(cacheName);
        tokenCaches.remove(userId);
        log.info("Cleared all cache entries for token='{}'", userId);
    }

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

    public static void close() {
        cacheManager.close();
        tokenCaches.clear();
        lastAccessMap.clear();
        cleaner.shutdownNow();
        log.info("CacheManager closed and all caches cleared");
    }

    private record Container(Object data) implements Serializable {
    }
}
