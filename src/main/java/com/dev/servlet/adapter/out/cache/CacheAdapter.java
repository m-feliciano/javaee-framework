package com.dev.servlet.adapter.out.cache;

import com.dev.servlet.application.port.out.cache.CachePort;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@ApplicationScoped
public class CacheAdapter implements CachePort {
    private static final String MAIN_CACHE = "application-cache";
    private static final Duration ttl = Duration.ofHours(1);
    private static final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();
    private static final Map<String, Long> tmpStore = new java.util.concurrent.ConcurrentHashMap<>();

    static {
        cleaner.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();

            for (Map.Entry<String, Long> entry : tmpStore.entrySet()) {
                if (entry.getValue() < now) {
                    tmpStore.remove(entry.getKey());
                }
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    private CacheManager cacheManager;
    private Cache<String, Object> cache;

    @PostConstruct
    void init() {
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache(
                        MAIN_CACHE,
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                String.class,
                                Object.class,
                                ResourcePoolsBuilder.heap(10_000)
                        ).withExpiry(
                                ExpiryPolicyBuilder.timeToLiveExpiration(ttl)
                        )
                )
                .build(true);

        cache = cacheManager.getCache(MAIN_CACHE, String.class, Object.class);

        log.info("Cache initialized with TTL {}", ttl);
    }

    @Override
    public void set(String namespace, String key, Object value) {
        String cacheKey = compoundKey(namespace, key);
        cache.put(cacheKey, value);
    }

    @Override
    public void set(String namespace, String key, Object value, Duration cacheTtl) {
        String cacheKey = compoundKey(namespace, key);
        cache.put(cacheKey, value);

        long expireAt = System.currentTimeMillis() + Math.min(cacheTtl.toMillis(), ttl.toMillis());
        tmpStore.put(cacheKey, expireAt);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String namespace, String key) {
        String cacheKey = compoundKey(namespace, key);
        return (T) cache.get(cacheKey);
    }

    @Override
    public void clear(String namespace, String key) {
        String cacheKey = compoundKey(namespace, key);
        cache.remove(cacheKey);
    }

    @Override
    public void clearNamespace(String namespace) {
        cache.forEach(entry -> {
            if (entry.getKey().startsWith(namespace + ":")) {
                cache.remove(entry.getKey());
            }
        });
    }

    public void clearAll(String key) {
        for (Cache.Entry<String, Object> entry : cache) {
            if (entry.getKey().endsWith(":" + key)) {
                cache.remove(entry.getKey());
            }
        }
    }

    @PreDestroy
    void shutdown() {
        cacheManager.close();
        log.info("CacheManager closed");
    }

    private String compoundKey(String namespace, String key) {
        return namespace + ":" + key;
    }
}
