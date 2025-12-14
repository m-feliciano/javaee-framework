package com.dev.servlet.domain.vo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public record BinaryPayload(String tempPath, Long size, String contentType) {

    public InputStream openStream() throws IOException {
        return Files.newInputStream(Path.of(tempPath));
    }

    public void close() {
        try {
            Files.deleteIfExists(Path.of(tempPath));
        } catch (IOException ignored) {
        }
    }
}
