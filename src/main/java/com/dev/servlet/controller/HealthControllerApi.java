package com.dev.servlet.controller;

import com.dev.servlet.core.annotation.Controller;
import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.response.IHttpResponse;

import java.util.Map;

import static com.dev.servlet.domain.model.enums.RequestMethod.GET;

@Controller("health")
public interface HealthControllerApi {

    @RequestMapping(value = "/check", method = GET)
    IHttpResponse<Map<String, Object>> health();

    @RequestMapping(value = "/ready", method = GET)
    IHttpResponse<Map<String, Object>> readiness();

    @RequestMapping(value = "/live", method = GET)
    IHttpResponse<Map<String, Object>> liveness();
}

