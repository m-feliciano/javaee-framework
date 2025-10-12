// src/main/java/com/dev/servlet/core/validator/RequestValidator.java
package com.dev.servlet.core.validator;

import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.util.EndpointParser;
import com.dev.servlet.core.validator.internal.ApiVersionValidationHandler;
import com.dev.servlet.core.validator.internal.AuthValidationHandler;
import com.dev.servlet.core.validator.internal.ConstraintValidationHandler;
import com.dev.servlet.core.validator.internal.MethodValidationHandler;
import com.dev.servlet.core.validator.internal.RoleValidationHandler;
import com.dev.servlet.domain.transfer.Request;

import java.util.ArrayList;
import java.util.List;

public final class RequestValidator {

    private final List<ValidationHandler> handlers = new ArrayList<>();

    public RequestValidator(EndpointParser endpoint) {
        handlers.add(new MethodValidationHandler());
        handlers.add(new AuthValidationHandler());
        handlers.add(new RoleValidationHandler());
        handlers.add(new ApiVersionValidationHandler(endpoint));
        handlers.add(new ConstraintValidationHandler());
    }

    public static void validate(EndpointParser endpoint, RequestMapping mapping, Request request) throws ServiceException {
        RequestValidator validator = new RequestValidator(endpoint);
        validator.validate(mapping, request);
    }

    public void validate(RequestMapping mapping, Request request) throws ServiceException {
        for (ValidationHandler handler : handlers) {
            handler.validate(mapping, request);
        }
    }
}
