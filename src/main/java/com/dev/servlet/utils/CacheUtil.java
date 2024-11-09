package com.dev.servlet.utils;

import com.dev.servlet.dto.UserDto;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO add ehCache, set a time policy
public final class CacheUtil {
    // token, cacheKey, collection
    private static final Map<String, Map<String, Collection<?>>> IN_MEMORY_CACHE = Collections.synchronizedMap(new HashMap<>());

    private CacheUtil() {
    }

    public static void set(String key, String token, Collection<? extends Serializable> collection) {
        IN_MEMORY_CACHE.putIfAbsent(token, new HashMap<>());
        String cacheKey = getCacheKey(key, token);
        getHash(token).put(cacheKey, collection);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> get(String key, String token) {
        IN_MEMORY_CACHE.putIfAbsent(token, new HashMap<>());
        String cacheKey = getCacheKey(key, token);
        return (List<T>) getHash(token).get(cacheKey);
    }

    public static void clear(String key, String token) {
        String cacheKey = getCacheKey(key, token);
        getHash(token).remove(cacheKey);
    }

    public static void clearAll(String token) {
        IN_MEMORY_CACHE.remove(token);
    }

    private static Map<String, Collection<?>> getHash(String token) {
        return IN_MEMORY_CACHE.get(token);
    }

    private static String getCacheKey(String key, String token) {
        return "%s_%s".formatted(key, token);
    }
}
