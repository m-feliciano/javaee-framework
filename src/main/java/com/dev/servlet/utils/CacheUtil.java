package com.dev.servlet.utils;

import static com.dev.servlet.utils.ObjectUtils.cloneObject;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.dev.servlet.domain.User;

public final class CacheUtil {

	private static final Map<String, Map<String, Collection<?>>> cacheMap = new HashMap<>();
	private static final Map<String, User> tokens = new HashMap<>();

	private CacheUtil() {}

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

	public static User findUser(String token) {
		if (tokens.containsKey(token)) {
			return cloneObject(tokens.get(token), User.class);
		}
		return null;
	}

	public static void init(String key, String token, Collection<?> collection) {
		cacheMap.put(token, getCollectionMap(key, collection));
	}

	@SuppressWarnings("unchecked")
	public static <T> Collection<T> get(String key, String token) {
		if (cacheMap.containsKey(token)) {
			return (Collection<T>) Collections
					.unmodifiableCollection((Collection<? extends T>) cacheMap.get(key));
		}

		return Collections.emptyList();
	}

	public static void clear(String key, String token) {
		if (cacheMap.containsKey(token) && cacheMap.get(token).containsKey(key))
			cacheMap.get(token).remove(key);
	}

	public static void clearAll(String token) {
		if (cacheMap.containsKey(token))
			cacheMap.remove(token);
	}

	public static void reset() {
		cacheMap.clear();
	}

	private static Map<String, Collection<?>> getCollectionMap(String key, Collection<?> elements) {
		return Map.of(key, elements);
	}
}
