package com.dev.servlet.infrastructure.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VirtualFileUtils {

    public static Path createTempFile(String json, String filename) throws IOException {
        Path tmpDir = Paths.get(System.getProperty("java.io.tmpdir"));
        Path file = tmpDir.resolve(filename);
        Files.writeString(file, json,
                StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        file.toFile().deleteOnExit();
        return file;
    }

    public static Path readTempFile(String fileName) {
        Path tmpDir = Paths.get(System.getProperty("java.io.tmpdir"));
        Path path = tmpDir.resolve(fileName);
        return Files.exists(path) ? path : null;
    }

    public static Path readResourceFile(String resourcePath) {
        URL resource = Objects.requireNonNull(
                VirtualFileUtils.class.getClassLoader().getResource(resourcePath)
        );
        try {
            return Paths.get(resource.toURI());
        } catch (Exception e) {
            throw new RuntimeException("Failed to access resource: " + resourcePath, e);
        }
    }

}
