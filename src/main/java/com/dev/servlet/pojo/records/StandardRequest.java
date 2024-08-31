package com.dev.servlet.pojo.records;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Immutable class
public record StandardRequest(HttpServletRequest servletRequest,
                              HttpServletResponse servletResponse,
                              RequestObject requestObject) {

    public String token() {
        return requestObject.token();
    }

    public String service() {
        return requestObject.service();
    }

    public String action() {
        return requestObject.action();
    }

    public Pagable pagination() {
        return requestObject.pagination();
    }
}