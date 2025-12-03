package com.dev.servlet.adapter.in.web.validator.internal;

import com.dev.servlet.adapter.in.web.annotation.RequestMapping;
import com.dev.servlet.adapter.in.web.dto.Request;
import com.dev.servlet.adapter.in.web.validator.ValidationHandler;
import com.dev.servlet.application.exception.ApplicationException;
import jakarta.servlet.http.HttpServletResponse;

import static com.dev.servlet.infrastructure.utils.ThrowableUtils.serviceError;

public class AuthValidationHandler implements ValidationHandler {
    @Override
    public void validate(RequestMapping mapping, Request request) throws ApplicationException {
        if (mapping.requestAuth() && request.getToken() == null) {
            throw serviceError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required.");
        }
    }
}
