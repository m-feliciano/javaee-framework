package com.dev.servlet.adapter.in.web.validator.internal;

import com.dev.servlet.adapter.in.web.annotation.RequestMapping;
import com.dev.servlet.adapter.in.web.dto.Request;
import com.dev.servlet.adapter.in.web.util.EndpointParser;
import com.dev.servlet.adapter.in.web.validator.ValidationHandler;
import com.dev.servlet.application.exception.AppException;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

public record ApiVersionValidationHandler(EndpointParser endpoint) implements ValidationHandler {
    @Override
    public void validate(RequestMapping mapping, Request request) throws AppException {
        if (!mapping.apiVersion().equals(endpoint.apiVersion())) {
            throw new AppException(SC_BAD_REQUEST, "API Not implemented");
        }
    }
}
