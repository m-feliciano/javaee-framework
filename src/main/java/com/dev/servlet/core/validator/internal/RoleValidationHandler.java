package com.dev.servlet.core.validator.internal;

import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.util.CollectionUtils;
import com.dev.servlet.core.util.JwtUtil;
import com.dev.servlet.core.validator.ValidationHandler;
import com.dev.servlet.domain.model.enums.RoleType;
import com.dev.servlet.domain.transfer.Request;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.dev.servlet.core.util.ThrowableUtils.serviceError;

public class RoleValidationHandler implements ValidationHandler {

    private final static JwtUtil jwtUtil = new JwtUtil();

    @Override
    public void validate(RequestMapping mapping, Request request) throws ServiceException {
        if (CollectionUtils.isEmpty(mapping.roles())) return;

        List<Long> roles = jwtUtil.getUserPerfisFromToken(request.getToken());
        for (RoleType role : mapping.roles()) {
            if (!roles.contains(role.getCode())) {
                throw serviceError(HttpServletResponse.SC_FORBIDDEN, "User does not have permission to access this endpoint");
            }
        }
    }
}
