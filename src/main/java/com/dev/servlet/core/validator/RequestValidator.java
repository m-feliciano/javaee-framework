// src/main/java/com/dev/servlet/core/validator/RequestValidator.java
package com.dev.servlet.core.validator;

import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.util.EndpointParser;
import com.dev.servlet.core.util.JwtUtil;
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

        public RequestValidator(EndpointParser endpoint, JwtUtil jwtUtil) {
        // The order of validation handlers is intentional:
        // 1. ApiVersionValidationHandler runs first to quickly reject requests with invalid or unsupported API versions,
        //    preventing unnecessary processing of requests that cannot be handled.
        handlers.add(new ApiVersionValidationHandler(endpoint));
        // 2. MethodValidationHandler checks HTTP method validity.
        handlers.add(new MethodValidationHandler());
        // 3. AuthValidationHandler ensures authentication is present.
        handlers.add(new AuthValidationHandler());
        // 4. RoleValidationHandler checks user roles/permissions.
        handlers.add(new RoleValidationHandler(jwtUtil));
        // 5. ConstraintValidationHandler validates request constraints.
        handlers.add(new ConstraintValidationHandler());
    }

    public void validate(RequestMapping mapping, Request request) throws ServiceException {
        for (ValidationHandler handler : handlers) {
            handler.validate(mapping, request);
        }
    }
}
