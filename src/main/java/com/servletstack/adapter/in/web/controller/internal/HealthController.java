package com.servletstack.adapter.in.web.controller.internal;

import com.servletstack.adapter.in.web.controller.HealthControllerApi;
import com.servletstack.adapter.in.web.controller.internal.base.BaseController;
import com.servletstack.adapter.in.web.dto.HttpResponse;
import com.servletstack.adapter.in.web.dto.IHttpResponse;
import com.servletstack.application.transfer.response.HealthStatus;
import com.servletstack.infrastructure.health.HealthService;
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
    private HealthService service;

    @Override
    protected Class<HealthController> implementation() {
        return HealthController.class;
    }

    public IHttpResponse<Map<String, Object>> health() {
        Map<String, Object> health = service.getHealthStatus();
        return HttpResponse.ok(health).next(HEALTH_PAGE).build();
    }

    @Override
    public IHttpResponse<Map<String, Object>> readiness() {
        Map<String, Object> ready = service.getReadinessStatus();
        return HttpResponse.ok(ready).next(HEALTH_PAGE).build();
    }

    @Override
    public IHttpResponse<Map<String, Object>> liveness() {
        Map<String, Object> live = service.getLivenessStatus();
        return HttpResponse.ok(live).next(HEALTH_PAGE).build();
    }

    @Override
    public HttpResponse<HealthStatus> up() {
        boolean isUp = service.isDatabaseHealthy() && service.isCacheHealthy();
        return HttpResponse.ok(new HealthStatus(isUp ? "UP" : "DOWN")).build();
    }
}
