package com.dev.servlet.core.validator.internal;

import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.validator.ValidationHandler;
import com.dev.servlet.domain.transfer.Request;

import javax.servlet.http.HttpServletResponse;

import static com.dev.servlet.core.util.CryptoUtils.isValidToken;
import static com.dev.servlet.core.util.ThrowableUtils.serviceError;

public class AuthValidationHandler implements ValidationHandler {

    @Override
    public void validate(RequestMapping mapping, Request request) throws ServiceException {
        if (mapping.requestAuth() && !isValidToken(request.getToken())) {
            throw serviceError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required.");
        }
    }
}
