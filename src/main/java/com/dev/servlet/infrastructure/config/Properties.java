package com.dev.servlet.infrastructure.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
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

    public static String get(String key) {
        return propertiesCache.computeIfAbsent(key, k -> {
            Object value = getNestedValue(k);
            return value != null ? value.toString() : null;
        });
    }

    public static <T> T getOrDefault(String key, T defaultValue) {
        String property = get(key);
        T value = parseProperty(property, defaultValue);
        return ObjectUtils.getIfNull(value, defaultValue);
    }

    public static String getEnvOrDefault(String env, String defaultValue) {
        String getenv = System.getenv(env);
        return ObjectUtils.getIfNull(getenv, defaultValue);
    }

    public static String getEnv(String env) {
        return getEnvOrDefault(env, null);
    }

    public static java.util.Properties loadDatabaseProperties() {
        String dbHost = System.getenv("DB_HOST");
        String dbPort = System.getenv("DB_PORT");
        String dbName = System.getenv("DB");
        String dbUser = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");
        if (dbHost == null || dbPort == null || dbName == null || dbUser == null || dbPassword == null) {
            throw new IllegalStateException("Database environment variables missing. " +
                                            "Required: DB_HOST, DB_PORT, DB, DB_USER, DB_PASSWORD");
        }
        String jdbcUrl = "jdbc:postgresql://%s:%s/%s".formatted(dbHost, dbPort, dbName);
        java.util.Properties databaseProps = new java.util.Properties();
        databaseProps.setProperty("jakarta.persistence.jdbc.url", jdbcUrl);
        databaseProps.setProperty("jakarta.persistence.jdbc.user", dbUser);
        databaseProps.setProperty("jakarta.persistence.jdbc.password", dbPassword);
        databaseProps.setProperty("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");
        return databaseProps;
    }

    public static String getAppBaseUrl() {
        return get("app.base_url");
    }

    public static String getAppDomain() {
        return get("app.domain");
    }

    public static boolean isDemoModeEnabled() {
        return BooleanUtils.toBoolean(getOrDefault("DEMO_MODE", "false"));
    }

    public static boolean isProductionMode() {
        return isMode("production");
    }

    public static boolean isDevelopmentMode() {
        return isMode("development");
    }

    private static boolean isMode(String mode) {
        return mode.equalsIgnoreCase(getOrDefault("app.env", "unknown"));
    }

    private static Map<String, Object> loadProperties() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource("");
        Objects.requireNonNull(url, "Resource URL is null");

        final String propFileName = ObjectUtils.getIfNull(System.getProperty("app.config.file"), "app.yml");

        try (InputStream in = loader.getResourceAsStream(propFileName)) {
            if (in == null) throw new RuntimeException("Config file not found: " + propFileName);
            Yaml yaml = new Yaml();
            return yaml.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration from file: " + propFileName, e);
        }
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

    public static java.util.Properties loadSmtpProperties() {
        var props = new java.util.Properties();

        props.put("mail.smtp.user", getEnv("SMTP_USER"));
        props.put("mail.smtp.pass", getEnv("SMTP_PASS"));

        props.put("mail.smtp.host", get("smtp.host"));
        props.put("mail.smtp.port", get("smtp.port"));
        props.put("mail.smtp.from.address", get("smtp.from.address"));
        props.put("mail.smtp.from.name", get("smtp.from.name"));

        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");
        return props;
    }
}
