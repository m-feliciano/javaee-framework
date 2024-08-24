package com.dev.servlet.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Immutable class
public record StandardRequest(HttpServletRequest servletRequest,
                              HttpServletResponse servletResponse,
                              String action,
                              Class<?> clazz,
                              String token,
                              StandardPagination pagination) {
}