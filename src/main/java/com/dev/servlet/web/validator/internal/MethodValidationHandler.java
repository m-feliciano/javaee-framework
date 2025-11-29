package com.dev.servlet.web.validator.internal;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.web.Request;
import com.dev.servlet.web.annotation.RequestMapping;
import com.dev.servlet.web.validator.ValidationHandler;
import jakarta.servlet.http.HttpServletResponse;

import static com.dev.servlet.shared.util.ThrowableUtils.serviceError;

public class MethodValidationHandler implements ValidationHandler {
    @Override
    public void validate(RequestMapping mapping, Request request) throws ApplicationException {
        if (!mapping.method().getMethod().equals(request.getMethod())) {
            throw serviceError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Method not allowed.");
        }
    }
}
