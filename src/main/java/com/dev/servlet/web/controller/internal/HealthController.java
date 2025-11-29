package com.dev.servlet.web.controller.internal;

import com.dev.servlet.infrastructure.health.HealthService;
import com.dev.servlet.web.controller.HealthControllerApi;
import com.dev.servlet.web.controller.internal.base.BaseController;
import com.dev.servlet.web.response.HttpResponse;
import com.dev.servlet.web.response.IHttpResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@NoArgsConstructor
@ApplicationScoped
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
    public HttpResponse<String> up() {
        boolean isUp = healthService.isDatabaseHealthy() && healthService.isCacheHealthy();
        String json = "{\"status\":\"" + (isUp ? "UP" : "DOWN") + "\"}";
        return HttpResponse.ofJson(json);
    }
}
