package com.dev.servlet.utils;

import java.util.Collection;

public final class CollectionUtils {

    private CollectionUtils() {
    }

    public static boolean isNullOrEmpty(Collection<?> array) {
        return array == null || array.size() == 0;
    }
}
