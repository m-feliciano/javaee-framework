package com.dev.servlet.web.validator.internal;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.port.out.AuthenticationPort;
import com.dev.servlet.domain.entity.enums.RoleType;
import com.dev.servlet.shared.util.CollectionUtils;
import com.dev.servlet.shared.util.RoleGroup;
import com.dev.servlet.web.Request;
import com.dev.servlet.web.annotation.RequestMapping;
import com.dev.servlet.web.validator.ValidationHandler;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.dev.servlet.shared.util.ThrowableUtils.serviceError;

public record RoleValidationHandler(AuthenticationPort authenticationPort) implements ValidationHandler {
    @Override
    public void validate(RequestMapping mapping, Request request) throws ApplicationException {
        if (!mapping.requestAuth() || mapping.roles().length == 0) return;

        List<Integer> roles = authenticationPort.extractRoles(request.getToken());
        if (!CollectionUtils.isEmpty(roles)) {
            List<RoleType> required = List.of(mapping.roles());
            Set<RoleType> userRoles = roles.stream().map(RoleType::toEnum).collect(Collectors.toSet());
            if (userRoles.stream().anyMatch(r -> RoleGroup.get(r).stream().anyMatch(required::contains)))
                return;
        }

        throw serviceError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
    }
}
