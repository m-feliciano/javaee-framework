package com.dev.servlet.application.port.out.cache;

import java.time.Duration;

public interface CachePort {
    void set(String namespace, String key, Object object);

    void set(String namespace, String key, Object object, Duration ttl);

    <T> T get(String namespace, String key);

    void clear(String namespace, String key);

    void clearAll(String key);

    void clearNamespace(String namespace);
}
