package com.dev.servlet.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CacheUtil {

	private static final Map<Map<String, String>, List<?>> cacheMap = new HashMap<>();
	private static final Set<String> tokens = new HashSet<>();

	private CacheUtil() {
	}

	public static void storeToken(String token) {
		tokens.add(token);
	}

	public static boolean hasToken(String token) {
		return tokens.contains(token);
	}

	public static void removeToken(String token) {
		if (token != null)
			tokens.remove(token);
	}

	public static void init(String key, String userKey, List<?> list) {
		Map<String, String> map = getKey(key, userKey);
		cacheMap.put(map, list);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> get(String key, String userKey) {
		Map<String, String> map = getKey(key, userKey);
		if (cacheMap.containsKey(map)) {
			return (List<T>) Collections.unmodifiableList(cacheMap.get(map));
		}

		return Collections.emptyList();
	}

	public static void clear(String key, String userKey) {
		Map<String, String> map = getKey(key, userKey);
		cacheMap.remove(map);
	}

	private static Map<String, String> getKey(String key, String userKey) {
		Map<String, String> map = new HashMap<>();
		map.put(key, userKey);
		return map;
	}
}
