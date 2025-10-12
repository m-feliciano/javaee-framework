package com.dev.servlet.core.validator.internal;

import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.util.CollectionUtils;
import com.dev.servlet.core.util.CryptoUtils;
import com.dev.servlet.core.validator.ValidationHandler;
import com.dev.servlet.domain.model.User;
import com.dev.servlet.domain.model.enums.RoleType;
import com.dev.servlet.domain.transfer.Request;

import javax.servlet.http.HttpServletResponse;

import static com.dev.servlet.core.util.ThrowableUtils.serviceError;

public class RoleValidationHandler implements ValidationHandler {

    @Override
    public void validate(RequestMapping mapping, Request request) throws ServiceException {
        if (CollectionUtils.isEmpty(mapping.roles())) return;

        User user = CryptoUtils.getUser(request.getToken());
        for (RoleType role : mapping.roles()) {
            if (!user.hasRole(role)) {
                throw serviceError(HttpServletResponse.SC_FORBIDDEN, "User does not have permission to access this endpoint");
            }
        }
    }
}
