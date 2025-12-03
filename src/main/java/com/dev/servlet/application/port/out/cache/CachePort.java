package com.dev.servlet.application.port.out.cache;

import java.util.Collection;
import java.util.List;

public interface CachePort {
    <T> void set(String key, String cacheName, Collection<T> collection);

    <T> void setObject(String key, String cacheName, T object);

    <T> List<T> get(String key, String cacheName);

    <T> T getObject(String key, String cacheName);

    void clear(String key, String cacheName);

    void clearAll(String key);

    void close();
}
