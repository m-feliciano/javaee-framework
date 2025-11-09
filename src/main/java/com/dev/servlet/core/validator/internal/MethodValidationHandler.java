package com.dev.servlet.core.validator.internal;

import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.validator.ValidationHandler;
import com.dev.servlet.domain.request.Request;

import javax.servlet.http.HttpServletResponse;

import static com.dev.servlet.core.util.ThrowableUtils.serviceError;

public class MethodValidationHandler implements ValidationHandler {

    @Override
    public void validate(RequestMapping mapping, Request request) throws ServiceException {
        if (!mapping.method().getMethod().equals(request.getMethod())) {
            throw serviceError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Method not allowed.");
        }
    }
}
