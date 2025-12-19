package com.dev.servlet.adapter.in.web.validator.internal;

import com.dev.servlet.adapter.in.web.annotation.RequestMapping;
import com.dev.servlet.adapter.in.web.dto.Request;
import com.dev.servlet.adapter.in.web.validator.ValidationHandler;
import com.dev.servlet.application.exception.AppException;

import static jakarta.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;

public class MethodValidationHandler implements ValidationHandler {
    @Override
    public void validate(RequestMapping mapping, Request request) throws AppException {
        if (!mapping.method().equals(request.getMethod())) {
            throw new AppException(SC_METHOD_NOT_ALLOWED, "Method not allowed.");
        }
    }
}
