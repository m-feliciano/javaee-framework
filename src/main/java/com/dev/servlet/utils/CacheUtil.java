package com.dev.servlet.utils;

import static com.dev.servlet.utils.ObjectUtils.cloneObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dev.servlet.domain.User;

public final class CacheUtil {

	private static final Map<String, List<?>> cacheMap = new HashMap<>();
	private static final Map<String, User> tokens = new HashMap<>();

	private CacheUtil() {
	}

	public static void storeToken(String token, User user) {
		tokens.put(token, user);
	}

	public static boolean hasToken(String token) {
		return tokens.containsKey(token);
	}

	public static void clearToken(String token) {
		if (token != null)
			tokens.remove(token);
	}

	public static User getUser(String token) {
		if (tokens.containsKey(token)) {
			return cloneObject(tokens.get(token));
		}
		return null;
	}

	public static void init(String key, String token, List<?> collection) {
		cacheMap.put(getCacheKey(key, token), collection);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> get(String key, String token) {
		String tokenKey = getCacheKey(key, token);
		if (!cacheMap.containsKey(tokenKey)) {
			return Collections.emptyList();
		}
		return (List<T>) cacheMap.get(tokenKey);
	}

	public static void clear(String key, String token) {
		String tokenKey = getCacheKey(key, token);
		cacheMap.remove(tokenKey);
	}

	public static void resetAll() {
		cacheMap.clear();
	}

	private static String getCacheKey(String key, String token) {
		return new StringBuilder().append(key).append(token).toString();
	}
}
