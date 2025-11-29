package com.dev.servlet.web.validator;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.port.out.AuthenticationPort;
import com.dev.servlet.shared.util.EndpointParser;
import com.dev.servlet.web.Request;
import com.dev.servlet.web.annotation.RequestMapping;
import com.dev.servlet.web.validator.internal.ApiVersionValidationHandler;
import com.dev.servlet.web.validator.internal.AuthValidationHandler;
import com.dev.servlet.web.validator.internal.ConstraintValidationHandler;
import com.dev.servlet.web.validator.internal.MethodValidationHandler;
import com.dev.servlet.web.validator.internal.RoleValidationHandler;

import java.util.ArrayList;
import java.util.List;

public final class RequestValidator {
    private final List<ValidationHandler> handlers = new ArrayList<>();

    public RequestValidator(EndpointParser endpoint, AuthenticationPort authenticationPort) {
            // The order matters
            handlers.add(new ApiVersionValidationHandler(endpoint));
            handlers.add(new MethodValidationHandler());
            handlers.add(new AuthValidationHandler());
        handlers.add(new RoleValidationHandler(authenticationPort));
            handlers.add(new ConstraintValidationHandler());
    }

    public void validate(RequestMapping mapping, Request request) throws ApplicationException {
        for (ValidationHandler handler : handlers) {
            handler.validate(mapping, request);
        }
    }
}
