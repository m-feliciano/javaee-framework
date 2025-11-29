package com.dev.servlet.web.validator.internal;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.web.Request;
import com.dev.servlet.web.annotation.RequestMapping;
import com.dev.servlet.web.validator.ValidationHandler;
import jakarta.servlet.http.HttpServletResponse;

import static com.dev.servlet.shared.util.ThrowableUtils.serviceError;

public class AuthValidationHandler implements ValidationHandler {
    @Override
    public void validate(RequestMapping mapping, Request request) throws ApplicationException {
        if (mapping.requestAuth() && request.getToken() == null) {
            throw serviceError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required.");
        }
    }
}
