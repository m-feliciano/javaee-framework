package com.dev.servlet.controller.internal;

import com.dev.servlet.controller.HealthControllerApi;
import com.dev.servlet.controller.base.BaseController;
import com.dev.servlet.core.response.HttpResponse;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.service.HealthService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Slf4j
@NoArgsConstructor
@Singleton
public class HealthController extends BaseController implements HealthControllerApi {
    private static final String HEALTH_PAGE = "forward:pages/health/health.jsp";

    @Inject
    private HealthService healthService;

    public IHttpResponse<Map<String, Object>> health() {
        Map<String, Object> health = healthService.getHealthStatus();
        return HttpResponse.ok(health).next(HEALTH_PAGE).build();
    }

    @Override
    public IHttpResponse<Map<String, Object>> readiness() {
        Map<String, Object> ready = healthService.getReadinessStatus();
        return HttpResponse.ok(ready).next(HEALTH_PAGE).build();
    }

    @Override
    public IHttpResponse<Map<String, Object>> liveness() {
        Map<String, Object> live = healthService.getLivenessStatus();
        return HttpResponse.ok(live).next(HEALTH_PAGE).build();
    }

    @Override
    public IHttpResponse<Boolean> up() {
        boolean isUp = healthService.isDatabaseHealthy() && healthService.isCacheHealthy();
        return HttpResponse.ok(isUp).build();
    }
}
