package com.servletstack.adapter.in.web.validator;

import com.servletstack.adapter.in.web.annotation.RequestMapping;
import com.servletstack.adapter.in.web.dto.Request;
import com.servletstack.adapter.in.web.util.EndpointParser;
import com.servletstack.adapter.in.web.validator.internal.ApiVersionValidationHandler;
import com.servletstack.adapter.in.web.validator.internal.AuthValidationHandler;
import com.servletstack.adapter.in.web.validator.internal.ConstraintValidationHandler;
import com.servletstack.adapter.in.web.validator.internal.MethodValidationHandler;
import com.servletstack.adapter.in.web.validator.internal.RoleValidationHandler;
import com.servletstack.application.exception.AppException;
import com.servletstack.application.port.out.security.AuthenticationPort;

import java.util.ArrayList;
import java.util.List;

public final class RequestValidator {
    private final List<ValidationHandler> handlers = new ArrayList<>();

    public RequestValidator(EndpointParser endpoint, AuthenticationPort authenticationPort) {
        // The order of handlers is important
        handlers.add(new ApiVersionValidationHandler(endpoint));
        handlers.add(new MethodValidationHandler());
        handlers.add(new AuthValidationHandler());
        handlers.add(new RoleValidationHandler(authenticationPort));
        handlers.add(new ConstraintValidationHandler());
    }

    public void validate(RequestMapping mapping, Request request) throws AppException {
        for (ValidationHandler handler : handlers) {
            handler.validate(mapping, request);
        }
    }
}
