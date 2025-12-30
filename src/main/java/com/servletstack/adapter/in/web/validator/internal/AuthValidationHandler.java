package com.servletstack.adapter.in.web.validator.internal;

import com.servletstack.adapter.in.web.annotation.RequestMapping;
import com.servletstack.adapter.in.web.dto.Request;
import com.servletstack.adapter.in.web.validator.ValidationHandler;
import com.servletstack.application.exception.AppException;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

public class AuthValidationHandler implements ValidationHandler {
    @Override
    public void validate(RequestMapping mapping, Request request) throws AppException {
        if (mapping.requestAuth() && request.getToken() == null) {
            throw new AppException(SC_UNAUTHORIZED, "Authentication required.");
        }
    }
}
