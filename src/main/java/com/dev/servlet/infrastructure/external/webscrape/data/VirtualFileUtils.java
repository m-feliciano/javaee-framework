package com.dev.servlet.infrastructure.external.webscrape.data;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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
}
