package com.dev.servlet.web.validator.internal;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.shared.util.EndpointParser;
import com.dev.servlet.web.Request;
import com.dev.servlet.web.annotation.RequestMapping;
import com.dev.servlet.web.validator.ValidationHandler;
import jakarta.servlet.http.HttpServletResponse;

public record ApiVersionValidationHandler(EndpointParser endpoint) implements ValidationHandler {
    @Override
    public void validate(RequestMapping mapping, Request request) throws ApplicationException {
        if (!mapping.apiVersion().equals(endpoint.apiVersion())) {
            throw new ApplicationException(HttpServletResponse.SC_BAD_REQUEST, "API Not implemented");
        }
    }
}
