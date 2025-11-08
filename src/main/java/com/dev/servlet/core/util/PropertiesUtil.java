package com.dev.servlet.core.util;

import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class PropertiesUtil {
    private static final ConcurrentHashMap<String, String> propertiesCache = new ConcurrentHashMap<>();

    public static String getProperty(String key) {
        try {
            if (Objects.isNull(propertiesCache.get(key))) {
                Properties appProps = getProperties();
                String property = appProps.getProperty(key);

                if (property != null) {
                    while (property.contains("{") && property.contains("}")) {
                        String otherProperty = property.substring(property.indexOf("{") + 1, property.indexOf("}"));
                        String otherValue = appProps.getProperty(otherProperty);
                        property = property.replace("{" + otherProperty + "}", otherValue);
                    }

                    propertiesCache.put(key, property);
                }
            }

            return propertiesCache.get(key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Properties getProperties() throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL resourceUrl = loader.getResource("");
        Objects.requireNonNull(resourceUrl, "Resource URL is null");
        String propFileName = ObjectUtils.getIfNull(
                System.getProperty("app.config.file"), "app-prod.properties");
        String rootPath = resourceUrl.getPath();
        Properties appProps = new Properties();
        try (FileInputStream inStream = new FileInputStream(rootPath + propFileName)) {
            appProps.load(inStream);
        }
        return appProps;
    }

    public static <T> T getProperty(String key, T defaultValue) {
        String property = getProperty(key);
        T value = parseProperty(property, defaultValue);
        return ObjectUtils.getIfNull(value, defaultValue);
    }

    @SuppressWarnings("unchecked")
    private static <T> T parseProperty(String property, T defaultValue) {
        if (property == null) return defaultValue;
        try {
            T object = null;
            if (defaultValue instanceof String) {
                object = (T) property;
            } else if (defaultValue instanceof Integer) {
                object = (T) Integer.valueOf(property);
            } else if (defaultValue instanceof Long) {
                object = (T) Long.valueOf(property);
            } else if (defaultValue instanceof Boolean) {
                object = (T) Boolean.valueOf(property);
            } else if (defaultValue instanceof Double) {
                object = (T) Double.valueOf(property);
            } else if (defaultValue instanceof Collection<?> collection) {
                object = (T) getPropertyCollection(property, collection);
            }
            return object;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Collection<T> getPropertyCollection(String property, Collection<T> defaultValue) {
        String[] split = property.split(";");
        if (split.length == 0) return defaultValue;
        String[] trimmed = Arrays.stream(split).map(String::trim).toArray(String[]::new);
        T[] array = (T[]) new Object[trimmed.length];
        for (int i = 0; i < trimmed.length; i++) {
            array[i] = parseProperty(trimmed[i], defaultValue.iterator().next());
        }
        return List.of(array);
    }
}
