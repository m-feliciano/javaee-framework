package com.servletstack.adapter.in.web.controller;

import com.servletstack.adapter.in.web.annotation.Controller;
import com.servletstack.adapter.in.web.annotation.RequestMapping;
import com.servletstack.adapter.in.web.dto.HttpResponse;
import com.servletstack.adapter.in.web.dto.IHttpResponse;
import com.servletstack.application.transfer.response.HealthStatus;

import java.util.Map;

@Controller("health")
public interface HealthControllerApi {
    @RequestMapping(value = "/check", description = "Perform a comprehensive health check of the application.")
    IHttpResponse<Map<String, Object>> health();

    @RequestMapping(value = "/ready", description = "Check if the application is ready to handle requests.")
    IHttpResponse<Map<String, Object>> readiness();

    @RequestMapping(value = "/live", description = "Check if the application is alive and functioning.")
    IHttpResponse<Map<String, Object>> liveness();

    @RequestMapping(value = "/up", requestAuth = false, description = "Check if the application is up (database and cache health).")
    HttpResponse<HealthStatus> up();
}
