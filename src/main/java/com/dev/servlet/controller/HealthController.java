package com.dev.servlet.controller;

import com.dev.servlet.controller.base.BaseController;
import com.dev.servlet.core.annotation.Controller;
import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.response.HttpResponse;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.service.HealthService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Map;

import static com.dev.servlet.domain.model.enums.RequestMethod.GET;

@Slf4j
@NoArgsConstructor
@Controller("health")
public class HealthController extends BaseController {
    private static final String HEALTH_PAGE = "forward:pages/health/health.jsp";

    @Inject
    private HealthService healthService;

    @RequestMapping(value = "/check", method = GET)
    public IHttpResponse<Map<String, Object>> health() {
        Map<String, Object> health = healthService.getHealthStatus();
        return HttpResponse.ok(health).next(HEALTH_PAGE).build();
    }

    @RequestMapping(value = "/ready", method = GET)
    public IHttpResponse<Map<String, Object>> readiness() {
        Map<String, Object> ready = healthService.getReadinessStatus();
        return HttpResponse.ok(ready).next(HEALTH_PAGE).build();
    }

    @RequestMapping(value = "/live", method = GET)
    public IHttpResponse<Map<String, Object>> liveness() {
        Map<String, Object> live = healthService.getLivenessStatus();
        return HttpResponse.ok(live).next(HEALTH_PAGE).build();
    }
}

