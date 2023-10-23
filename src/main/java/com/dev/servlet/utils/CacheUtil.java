package com.dev.servlet.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CacheUtil {

	private static final Map<String, List<?>> cacheMap = new HashMap<>();
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

	public static void init(String key, String token, List<?> list) {
		var name = getKeyToken(key, token);
		cacheMap.put(name, list);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> get(String key, String token) {
		var name = getKeyToken(key, token);
		if (cacheMap.containsKey(name))
			return (List<T>) Collections.unmodifiableList(cacheMap.get(name));

		return Collections.emptyList();
	}

	public static void clear(String key, String token) {
		var name = getKeyToken(key, token);
		if (cacheMap.containsKey(name))
			cacheMap.remove(name);
	}

	private static String getKeyToken(String key, String token) {
		return token.concat(key);
	}
}
