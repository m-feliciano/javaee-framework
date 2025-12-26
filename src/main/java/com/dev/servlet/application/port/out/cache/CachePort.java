package com.dev.servlet.application.port.out.cache;

import java.time.Duration;
import java.util.UUID;

public interface CachePort {
    void set(String namespace, UUID key, Object object);

    void set(String namespace, UUID key, Object object, Duration ttl);

    <T> T get(String namespace, UUID key);

    void clear(String namespace, UUID key);

    void clearAll(UUID key);

    void clearNamespace(String namespace);

    void clearSuffix(String namespace, UUID key);
}
