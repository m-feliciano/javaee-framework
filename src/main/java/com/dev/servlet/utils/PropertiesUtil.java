package com.dev.servlet.utils;

import org.apache.commons.lang3.ObjectUtils;

import java.io.FileInputStream;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public final class PropertiesUtil {

    public static final String AUTHORIZED = "authorized";

    private PropertiesUtil() {
    }

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

    public static String getProperty(String key, String defaultValue) {
        String property = getProperty(key);
        return ObjectUtils.defaultIfNull(property, defaultValue);
    }

    public static Set<String> getAuthorizedActions() {
        Collection<String> actions = CacheUtil.get(AUTHORIZED, AUTHORIZED);
        if (CollectionUtils.isNullOrEmpty(actions)) {
            String props = getProperty("auth.authorized", "login,loginForm,register,registerPage");
            actions = List.of(props.split(","));
            CacheUtil.set(AUTHORIZED, AUTHORIZED, actions);
        }

        return Set.copyOf(actions);
    }
}
