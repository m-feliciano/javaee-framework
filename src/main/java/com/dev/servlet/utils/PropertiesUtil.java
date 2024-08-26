package com.dev.servlet.utils;

import org.apache.commons.lang3.ObjectUtils;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.Set;

public final class PropertiesUtil {

    private PropertiesUtil() {
    }

    /**
     * Gets property.
     *
     * @param key
     * @return the property
     */
    public static String getProperty(String key) {
        try {
            String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
            String appConfigPath = rootPath + "app.properties";
//            String catalogConfigPath = rootPath + "catalog";

            Properties appProps = new Properties();
            try (FileInputStream inStream = new FileInputStream(appConfigPath)) {
                appProps.load(inStream);
            }
//            Properties catalogProps = new Properties();
//            catalogProps.load(new FileInputStream(catalogConfigPath));

            return appProps.getProperty(key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets property.
     *
     * @param key          the key
     * @param defaultValue  the default value
     * @return the property
     */
    public static String getProperty(String key, String defaultValue) {
        String property = getProperty(key);
        return ObjectUtils.defaultIfNull(property, defaultValue);
    }

    /**
     * Gets authorized actions.
     *
     * @return the authorized actions
     */
    public static Set<String> getAuthorizedActions() {
        String props = getProperty("auth.authorized", "login,loginForm,register,registerPage");
        return Set.of(props.split(","));
    }

    /**
     * Returns true if rate limit is enabled.
     * It is enabled by default.
     * If the environment is development, it is disabled (there is no need to limit the requests).
     *
     */
    public static boolean isRateLimitEnabled() {
        if ("development".equalsIgnoreCase(getProperty("env"))) {
            return false;
        }

        String property = getProperty("rate.limit.enabled", "true");
        return Boolean.parseBoolean(property);
    }
}
