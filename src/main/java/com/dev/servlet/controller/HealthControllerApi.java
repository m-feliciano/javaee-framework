package com.dev.servlet.controller;

import com.dev.servlet.core.annotation.Controller;
import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.response.HttpResponse;
import com.dev.servlet.core.response.IHttpResponse;

import java.util.Map;

@Controller("health")
public interface HealthControllerApi {

    @RequestMapping(value = "/check")
    IHttpResponse<Map<String, Object>> health();

    @RequestMapping(value = "/ready")
    IHttpResponse<Map<String, Object>> readiness();

    @RequestMapping(value = "/live")
    IHttpResponse<Map<String, Object>> liveness();

    @RequestMapping(value = "/up", requestAuth = false)
    HttpResponse<String> up();
}

