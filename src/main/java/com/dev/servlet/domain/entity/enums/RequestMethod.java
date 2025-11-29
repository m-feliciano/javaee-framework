package com.dev.servlet.domain.entity.enums;

import lombok.Getter;

public enum RequestMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    PATCH("PATCH"),
    OPTIONS("OPTIONS");
    @Getter
    private final String method;

    RequestMethod(String method) {
        this.method = method;
    }

    public boolean isEquals(String method) {
        return this.method.equalsIgnoreCase(method);
    }
}
