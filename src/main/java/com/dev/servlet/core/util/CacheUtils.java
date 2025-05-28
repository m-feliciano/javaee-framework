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

    private static Cache<String, Container> getOrCreateCache(String token) {
        if (tokenCaches.containsKey(token)) {
            return tokenCaches.get(token);
        }
        String cacheName = "cache_" + token;
        cacheManager.removeCache(cacheName);
        CacheConfigurationBuilder<String, Container> config = cacheConfigurationBuilder();
        cacheManager.createCache(cacheName, config);
        log.info("Created new cache for token: {}", shortTokenForKey(token));
        Cache<String, Container> cache = cacheManager.getCache(cacheName, String.class, Container.class);
        tokenCaches.put(token, cache);
        lastAccessMap.put(token, System.currentTimeMillis());
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

    public static <T> void set(String key, String token, Collection<T> collection) {
        String shortToken = shortTokenForKey(token);
        List<T> data = CloneUtil.cloneList(collection);
        Cache<String, Container> cache = getOrCreateCache(shortToken);
        cache.put(key, new Container(data));
        log.debug("Cached data for key='{}', token='{}'", key, shortTokenForKey(token));
    }

    public static <T> void setObject(String key, String token, T object) {
        String shortToken = shortTokenForKey(token);
        Cache<String, Container> cache = getOrCreateCache(shortToken);
        T clone = CloneUtil.forceClone(object);
        Container container = new Container(clone);
        cache.put(key, container);
        log.debug("Cached object for key='{}', token='{}'", key, shortToken);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> get(String key, String token) {
        String shortToken = shortTokenForKey(token);
        Cache<String, Container> cache = getOrCreateCache(shortToken);
        Container value = cache.get(key);
        log.debug("Retrieved data for key='{}', token='{}': {}", key, shortToken, value != null ? "HIT" : "MISS");
        if (value != null) {
            Object data = value.data();
            if (data instanceof Collection<?> valueCollection) {
                return (List<T>) CloneUtil.cloneList(valueCollection);
            }
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public static <T> T getObject(String key, String token) {
        String shortToken = shortTokenForKey(token);
        Cache<String, Container> cache = getOrCreateCache(shortToken);
        Container value = cache.get(key);
        log.debug("Retrieved data for key='{}', token='{}': {}", key, shortToken, value != null ? "HIT" : "MISS");
        if (value == null) {
            return null;
        }
        return CloneUtil.forceClone((T) value.data());
    }

    public static void clear(String key, String token) {
        String shortToken = shortTokenForKey(token);
        Cache<String, Container> cache = getOrCreateCache(shortToken);
        cache.remove(key);
        log.info("Cleared cache entry for key='{}', token='{}'", key, shortToken);
    }

    public static void clearAll(String token) {
        String shortToken = shortTokenForKey(token);
        String cacheName = "cache_" + shortToken;
        cacheManager.removeCache(cacheName);
        tokenCaches.remove(shortToken);
        log.info("Cleared all cache entries for token='{}'", shortToken);
    }

    private static String shortTokenForKey(String token) {
        return token.substring(0, 25);
    }

    private static void cleanupUnusedCaches() {
        long now = System.currentTimeMillis();
        for (var entry : lastAccessMap.entrySet()) {
            if (now - entry.getValue() > TimeUnit.MINUTES.toMillis(CACHE_IDLE_TIMEOUT_MINUTES)) {
                String token = entry.getKey();
                String cacheName = "cache_" + token;
                cacheManager.removeCache(cacheName);
                tokenCaches.remove(token);
                lastAccessMap.remove(token);
                log.info("Evicted unused cache for token='{}'", token);
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

    public static void clearCacheKeyPrefix(String cacheKeyPrefix, String cacheToken) {
        String shortToken = shortTokenForKey(cacheToken);
        Cache<String, Container> cache = tokenCaches.get(shortToken);
        if (cache != null) {
            cache.forEach(entry -> {
                if (entry.getKey().startsWith(cacheKeyPrefix)) {
                    cache.remove(entry.getKey());
                    log.info("Cleared cache entry with prefix '{}' for token='{}'", cacheKeyPrefix, shortToken);
                }
            });
        } else {
            log.warn("No cache found for token='{}' to clear entries with prefix '{}'", shortToken, cacheKeyPrefix);
        }
    }

    private record Container(Object data) implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }
}
