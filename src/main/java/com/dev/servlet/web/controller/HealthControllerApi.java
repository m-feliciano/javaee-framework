package com.dev.servlet.web.controller;

import com.dev.servlet.web.annotation.Controller;
import com.dev.servlet.web.annotation.RequestMapping;
import com.dev.servlet.web.response.HttpResponse;
import com.dev.servlet.web.response.IHttpResponse;

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
