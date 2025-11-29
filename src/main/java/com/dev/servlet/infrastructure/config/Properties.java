package com.dev.servlet.infrastructure.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Properties {
    private final static ConcurrentHashMap<String, String> propertiesCache = new ConcurrentHashMap<>();
    private final static Map<String, Object> props = loadProperties();

    private static Map<String, Object> loadProperties() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource("");
        Objects.requireNonNull(url, "Resource URL is null");
        String propFileName = ObjectUtils.getIfNull(System.getProperty("app.config.file"), "app-prod.yml");
        try (InputStream in = loader.getResourceAsStream(propFileName)) {
            if (in == null) throw new RuntimeException("Config file not found: " + propFileName);
            Yaml yaml = new Yaml();
            return yaml.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration from file: " + propFileName, e);
        }
    }

    public static String get(String key) {
        return propertiesCache.computeIfAbsent(key, k -> {
            Object value = getNestedValue(k);
            return value != null ? value.toString() : null;
        });
    }

    public static <T> T getOrDefault(String key, T defaultValue) {
        String property = get(key);
        T value = parseProperty(property, defaultValue);
        return value != null ? value : defaultValue;
    }

    private static Object getNestedValue(String key) {
        String[] parts = key.split("\\.");
        Object current = props;
        for (String part : parts) {
            if (!(current instanceof Map<?, ?> currentMap))
                return null;
            current = currentMap.get(part);
            if (current == null)
                return null;
        }
        return current;
    }

    @SuppressWarnings("unchecked")
    private static <T> T parseProperty(String property, T defaultValue) {
        if (property == null) return defaultValue;
        try {
            if (defaultValue instanceof String) {
                return (T) property;
            } else if (defaultValue instanceof Integer) {
                return (T) Integer.valueOf(property);
            } else if (defaultValue instanceof Long) {
                return (T) Long.valueOf(property);
            } else if (defaultValue instanceof Boolean) {
                return (T) Boolean.valueOf(property);
            } else if (defaultValue instanceof Double) {
                return (T) Double.valueOf(property);
            } else if (defaultValue instanceof Collection<?> collection) {
                return (T) getPropertyCollection(property, collection);
            }
        } catch (Exception e) {
            return defaultValue;
        }
        return defaultValue;
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

    public static String getEnvOrDefault(String env, String defaultValue) {
        return propertiesCache.computeIfAbsent(env, k -> {
            String value = System.getenv(k);
            return value != null ? value : defaultValue;
        });
    }

    public static java.util.Properties loadDatabaseProperties() {
        String dbHost = System.getenv("DB_HOST");
        String dbPort = System.getenv("DB_PORT");
        String dbName = System.getenv("POSTGRES_DB");
        String dbUser = System.getenv("POSTGRES_USER");
        String dbPassword = System.getenv("POSTGRES_PASSWORD");
        if (dbHost == null || dbPort == null || dbName == null || dbUser == null || dbPassword == null) {
            throw new IllegalStateException("Database environment variables missing. " +
                                            "Required: DB_HOST, DB_PORT, POSTGRES_DB, POSTGRES_USER, POSTGRES_PASSWORD");
        }
        String jdbcUrl = "jdbc:postgresql://%s:%s/%s".formatted(dbHost, dbPort, dbName);
        java.util.Properties databaseProps = new java.util.Properties();
        databaseProps.setProperty("jakarta.persistence.jdbc.url", jdbcUrl);
        databaseProps.setProperty("jakarta.persistence.jdbc.user", dbUser);
        databaseProps.setProperty("jakarta.persistence.jdbc.password", dbPassword);
        databaseProps.setProperty("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");
        return databaseProps;
    }
}
