package com.dev.servlet.utils;

import com.dev.servlet.dto.UserDto;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO add ehCache, set a time policy
public final class CacheUtil {
    // token, cacheKey, collection
    private static final Map<String, Map<String, List<?>>> IN_MEMORY_CACHE = Collections.synchronizedMap(new HashMap<>());
    private static final Map<String, UserDto> TOKENS = new HashMap<>();

    private CacheUtil() {
    }

    public static void storeToken(String token, UserDto user) {
        TOKENS.put(token, user);
    }

    public static boolean hasToken(String token) {
        return TOKENS.containsKey(token);
    }

    public static void clearToken(String token) {
        if (token != null) TOKENS.remove(token);
    }

    public static UserDto getUser(String token) {
        if (TOKENS.containsKey(token)) {
            return CloneUtils.clone(TOKENS.get(token));
        }
        return null;
    }

    public static void set(String key, String token, List<? extends Serializable> collection) {
        IN_MEMORY_CACHE.putIfAbsent(token, new HashMap<>());
        String cacheKey = getCacheKey(key, token);
        IN_MEMORY_CACHE.get(token).put(cacheKey, collection);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> get(String key, String token) {
        IN_MEMORY_CACHE.putIfAbsent(token, new HashMap<>());
        String cacheKey = getCacheKey(key, token);
        return (List<T>) IN_MEMORY_CACHE.get(token).get(cacheKey);
    }

    public static void clear(String key, String token) {
        String cacheKey = getCacheKey(key, token);
        IN_MEMORY_CACHE.get(token).remove(cacheKey);
    }

    private static String getCacheKey(String key, String token) {
        return key + token;
    }
}
