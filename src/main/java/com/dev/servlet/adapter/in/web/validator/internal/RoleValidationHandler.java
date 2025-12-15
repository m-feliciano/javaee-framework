package com.dev.servlet.adapter.in.web.validator.internal;

import com.dev.servlet.adapter.in.web.annotation.RequestMapping;
import com.dev.servlet.adapter.in.web.dto.Request;
import com.dev.servlet.adapter.in.web.validator.ValidationHandler;
import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.domain.entity.enums.RoleType;
import com.dev.servlet.shared.util.CollectionUtils;

import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;

public record RoleValidationHandler(AuthenticationPort authenticationPort) implements ValidationHandler {
    @Override
    public void validate(RequestMapping mapping, Request request) throws AppException {
        if (!mapping.requestAuth() || mapping.roles().length == 0) return;

        List<Integer> roles = authenticationPort.extractRoles(request.getToken());
        if (!CollectionUtils.isEmpty(roles)) {
            List<RoleType> required = List.of(mapping.roles());

            boolean match = roles.stream()
                    .map(RoleType::toEnum)
                    .anyMatch(r -> RoleType.getRoles(r).stream().anyMatch(required::contains));
            if (match) return;
        }

        throw new AppException(SC_FORBIDDEN, "Access Denied");
    }
}
