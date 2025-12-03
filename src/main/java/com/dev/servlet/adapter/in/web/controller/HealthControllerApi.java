package com.dev.servlet.adapter.in.web.controller;

import com.dev.servlet.adapter.in.web.annotation.Controller;
import com.dev.servlet.adapter.in.web.annotation.RequestMapping;
import com.dev.servlet.adapter.in.web.dto.HttpResponse;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;

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
