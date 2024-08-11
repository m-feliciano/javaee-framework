package com.dev.servlet.builders;

import com.dev.servlet.filter.StandardRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BusinessRequest {

    private final StandardRequest standardRequest;

    public static BusinessRequest builder() {
        return new BusinessRequest();
    }

    public BusinessRequest() {
        this.standardRequest = new StandardRequest();
    }

    public BusinessRequest token(String token) {
        standardRequest.setToken(token);
        return this;
    }

    public BusinessRequest clazz(Class<?> clazz) {
        standardRequest.setClazz(clazz);
        return this;
    }

    public BusinessRequest action(String action) {
        standardRequest.setAction(action);
        return this;
    }

    public BusinessRequest request(HttpServletRequest request) {
        standardRequest.setRequest(request);
        return this;
    }

    public BusinessRequest response(HttpServletResponse response) {
        standardRequest.setResponse(response);
        return this;
    }

    public StandardRequest build() {
        return this.standardRequest;
    }

}
