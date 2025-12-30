package com.servletstack.application.port.out.storage;

import java.net.URI;
import java.time.Duration;

public interface StorageService {

    URI generatePresignedUri(String path, Duration validity);

    URI generatePublicUri(String path);

    URI generateUploadUri(String path, String contentType, Duration validity);

    void deleteFile(String path);

    default URI generatePresignedUri(String path) {
        return generatePresignedUri(path, Duration.ofMinutes(10));
    }
}
