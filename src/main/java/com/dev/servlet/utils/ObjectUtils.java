package com.dev.servlet.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;

public final class ObjectUtils {

	private ObjectUtils() {
	}

	public static <T extends Serializable> T cloneObject(T object) {
		return SerializationUtils.clone(object);
	}

	public static <T extends Serializable> List<T> cloneList(List<T> objects) {
		List<T> list = new ArrayList<>();
		Iterator<T> iterator = objects.iterator();
		while (iterator.hasNext()) {
			T t = iterator.next();
			list.add(cloneObject(t));
		}
		return list;
	}
}
