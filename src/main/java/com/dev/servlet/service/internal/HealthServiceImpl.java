package com.dev.servlet.service.internal;

import com.dev.servlet.core.util.CacheUtils;
import com.dev.servlet.core.util.Properties;
import com.dev.servlet.service.HealthService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@NoArgsConstructor
@Singleton
public class HealthServiceImpl implements HealthService {

    @Inject
    private EntityManager entityManager;

    @Override
    public Map<String, Object> getHealthStatus() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("version", Properties.get("system.version"));
        health.put("service", Properties.get("name"));
        health.put("environment", Properties.getOrDefault("app.env", "unknown"));

        Map<String, String> components = new HashMap<>();
        components.put("database", isDatabaseHealthy() ? "UP" : "DOWN");
        components.put("cache", isCacheHealthy() ? "UP" : "DOWN");
        health.put("components", components);

        boolean allHealthy = isDatabaseHealthy() && isCacheHealthy();
        health.put("status", allHealthy ? "UP" : "DOWN");

        if (!allHealthy) {
            log.warn("Health check failed. Database: {}, Cache: {}",
                components.get("database"), components.get("cache"));
        }

        return health;
    }

    @Override
    public Map<String, Object> getReadinessStatus() {
        Map<String, Object> ready = new HashMap<>();
        ready.put("timestamp", System.currentTimeMillis());

        boolean dbReady = isDatabaseHealthy();
        boolean cacheReady = isCacheHealthy();

        ready.put("database_ready", dbReady);
        ready.put("cache_ready", cacheReady);
        ready.put("ready", dbReady && cacheReady);
        ready.put("status", "UP");

        if (!dbReady || !cacheReady) {
            log.warn("Readiness check failed. Database: {}, Cache: {}", dbReady, cacheReady);
        }

        return ready;
    }

    @Override
    public Map<String, Object> getLivenessStatus() {
        Map<String, Object> live = new HashMap<>();
        live.put("alive", true);
        live.put("timestamp", System.currentTimeMillis());
        live.put("uptime", ManagementFactory.getRuntimeMXBean().getUptime());
        live.put("status", "UP");

        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();

        Map<String, Object> memory = new HashMap<>();
        memory.put("used_mb", heapUsage.getUsed() / (1024 * 1024));
        memory.put("max_mb", heapUsage.getMax() / (1024 * 1024));
        memory.put("usage_percent", (heapUsage.getUsed() * 100) / heapUsage.getMax());
        live.put("memory", memory);

        long usagePercent = (heapUsage.getUsed() * 100) / heapUsage.getMax();
        if (usagePercent > 80) {
            log.warn("High memory usage detected: {}%", usagePercent);
        }

        return live;
    }

    @Override
    public boolean isDatabaseHealthy() {
        try {
            entityManager.createNativeQuery("SELECT 1").getSingleResult();
            log.debug("Database health check: PASSED");
            return true;
        } catch (Exception e) {
            log.error("Database health check: FAILED", e);
            return false;
        }
    }

    @Override
    public boolean isCacheHealthy() {
        try {
            String testKey = "health_check_test";
            CacheUtils.setObject(testKey, "health", "test_value");
            String result = CacheUtils.getObject(testKey, "health");
            CacheUtils.clear(testKey, "health");

            boolean healthy = "test_value".equals(result);
            log.debug("Cache health check: {}", healthy ? "PASSED" : "FAILED");
            return healthy;
        } catch (Exception e) {
            log.error("Cache health check: FAILED", e);
            return false;
        }
    }
}

