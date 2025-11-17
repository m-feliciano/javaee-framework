package com.dev.servlet.core.validator.internal;

import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.util.CollectionUtils;
import com.dev.servlet.core.util.JwtUtil;
import com.dev.servlet.core.util.RoleGroup;
import com.dev.servlet.core.validator.ValidationHandler;
import com.dev.servlet.domain.model.enums.RoleType;
import com.dev.servlet.domain.request.Request;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.dev.servlet.core.util.ThrowableUtils.serviceError;

public record RoleValidationHandler(JwtUtil jwts) implements ValidationHandler {

    @Override
    public void validate(RequestMapping mapping, Request request) throws ServiceException {
        if (!mapping.requestAuth() || mapping.roles().length == 0) return;

        List<Integer> roles = jwts.getRoles(request.getToken());
        if (!CollectionUtils.isEmpty(roles)) {
            List<RoleType> required = List.of(mapping.roles());
            Set<RoleType> userRoles = roles.stream().map(RoleType::toEnum).collect(Collectors.toSet());

            if (userRoles.stream().anyMatch(r -> RoleGroup.get(r).stream().anyMatch(required::contains)))
                return;
        }

        throw serviceError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
    }
}
