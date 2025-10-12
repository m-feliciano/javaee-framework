package com.dev.servlet.core.validator.internal;

import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.util.EndpointParser;
import com.dev.servlet.core.validator.ValidationHandler;
import com.dev.servlet.domain.transfer.Request;

import javax.servlet.http.HttpServletResponse;

public record ApiVersionValidationHandler(EndpointParser endpoint) implements ValidationHandler {

    @Override
    public void validate(RequestMapping mapping, Request request) throws ServiceException {
        if (!mapping.apiVersion().equals(endpoint.getApiVersion())) {
            throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, "API Not implemented");
        }
    }
}
