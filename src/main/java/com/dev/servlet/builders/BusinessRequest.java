package com.dev.servlet.builders;

import com.dev.servlet.filter.StandardRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class BusinessRequest {

    private final Map<String, Object> parameters;

    public static BusinessRequest builder() {
        return new BusinessRequest();
    }

    public BusinessRequest() {
        parameters = new java.util.HashMap<>();
    }

    public BusinessRequest token(String token) {
        this.parameters.put("token", token);
        return this;
    }

    public BusinessRequest clazz(Class<?> clazz) {
        this.parameters.put("clazz", clazz);
        return this;
    }

    public BusinessRequest action(String action) {
        this.parameters.put("action", action);
        return this;
    }

    public BusinessRequest request(HttpServletRequest request) {
        this.parameters.put("request", request);
        return this;
    }

    public BusinessRequest response(HttpServletResponse response) {
        this.parameters.put("response", response);
        return this;
    }

    public StandardRequest build() {
        return new StandardRequest(
                (HttpServletRequest) this.parameters.get("request"),
                (HttpServletResponse) this.parameters.get("response"),
                (String) this.parameters.get("action"),
                (Class<?>) this.parameters.get("clazz"),
                (String) this.parameters.get("token"));
    }

}
