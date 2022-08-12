package utils;

import com.mchange.util.AssertException;

import java.util.List;

public final class ArrayUtils {

    private ArrayUtils() {
        throw new AssertException("ArrayUtils is a utility class and should not be instantiated");
    }

    public static boolean isArrayNullOrEmpty(List<?> array) {
        return array == null || array.isEmpty();
    }
}
