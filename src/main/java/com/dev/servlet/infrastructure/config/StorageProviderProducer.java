package com.dev.servlet.infrastructure.config;

import com.dev.servlet.application.port.out.storage.StorageService;
import com.dev.servlet.infrastructure.annotations.StorageProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class StorageProviderProducer {

    @Produces
    @Default
    @ApplicationScoped
    public StorageService produce(
            @StorageProvider("s3") Instance<StorageService> s3ServiceInstance
    ) {
        String provider = Properties.getEnvOrDefault("STORAGE_PROVIDER", "s3");

        if ("s3".equalsIgnoreCase(provider)) {
            log.info("StorageService: Using S3Service as the storage provider.");
            return s3ServiceInstance.get();
        }

        throw new IllegalStateException("No StorageService for provider: " + provider);
    }
}
