package com.dev.servlet.infrastructure.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileUtil {

    public static Path readResourceFile(String resource) {
        URL url = FileUtil.class.getClassLoader().getResource(resource);
        Objects.requireNonNull(url, "Resource not found: " + resource);

        try {
            return Paths.get(url.toURI());
        } catch (Exception e) {
            throw new RuntimeException("Failed to access resource: " + resource, e);
        }
    }
}
