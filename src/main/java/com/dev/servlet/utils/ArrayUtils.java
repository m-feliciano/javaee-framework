package com.dev.servlet.utils;

import java.util.Collection;

public final class ArrayUtils {

	private ArrayUtils() {
	}

	public static boolean isArrayNullOrEmpty(Collection<?> array) {
		return array == null || array.size() == 0;
	}
}
