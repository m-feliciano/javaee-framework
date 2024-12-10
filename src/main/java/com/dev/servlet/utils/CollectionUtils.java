package com.dev.servlet.utils;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class CollectionUtils {

    public static boolean isEmpty(Collection<?> array) {
        return array == null || array.size() == 0;
    }

    /**
     * Join two collections into one.
     *
     * @param col1
     * @param col2
     * @param <T>
     * @return
     */
    public static <T> Collection<T> join(Collection<T> col1, Collection<T> col2) {
        Collection<T> result = new ArrayList<>(col1);
        result.addAll(col2);
        return result;
    }
}
