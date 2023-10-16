package com.dev.servlet.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CacheUtil {

	private static final Map<Map<String, String>, List<?>> syncCache = Collections.synchronizedMap(new HashMap<>());

	private CacheUtil() {
	}

	public static void initCache(String key, String userKey, List<?> list) {
		Map<String, String> map = getMapKey(key, userKey);
		syncCache.put(map, list);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> getFromCache(String key, String userKey) {
		Map<String, String> map = getMapKey(key, userKey);
		if (syncCache.containsKey(map)) {
			return (List<T>) Collections.unmodifiableList(syncCache.get(map));
		}

		return Collections.emptyList();
	}

	private static Map<String, String> getMapKey(String key, String userKey) {
		Map<String, String> map = new HashMap<>();
		map.put(key, userKey);
		return map;
	}

	public static void clearCache(String key, String userKey) {
		Map<String, String> map = getMapKey(key, userKey);
		syncCache.remove(map);
	}
}
