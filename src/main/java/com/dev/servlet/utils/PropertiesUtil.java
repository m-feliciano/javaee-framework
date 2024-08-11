package com.dev.servlet.utils;

import java.io.FileInputStream;
import java.util.Properties;

public final class PropertiesUtil {

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
}
