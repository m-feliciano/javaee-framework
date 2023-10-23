package com.dev.servlet.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class ObjectUtils {

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private ObjectUtils() {}

	public static <T> T cloneObject(Object object, Class<T> clazz) {
		return gson.fromJson(gson.toJson(object), clazz);
	}
}
