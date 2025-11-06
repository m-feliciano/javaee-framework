package com.dev.servlet.domain.service;

import java.util.Map;

public interface HealthService {
    Map<String, Object> getHealthStatus();
    Map<String, Object> getReadinessStatus();
    Map<String, Object> getLivenessStatus();
    boolean isDatabaseHealthy();
    boolean isCacheHealthy();
}

