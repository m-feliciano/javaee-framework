package com.dev.servlet.providers;

import com.dev.servlet.dto.ServiceException;
import com.dev.servlet.interfaces.RequestMapping;
import com.dev.servlet.interfaces.Validator;
import com.dev.servlet.pojo.ConstraintValidator;
import com.dev.servlet.pojo.domain.User;
import com.dev.servlet.pojo.enums.PerfilEnum;
import com.dev.servlet.pojo.records.Request;
import com.dev.servlet.utils.CollectionUtils;
import com.dev.servlet.utils.CryptoUtils;
import com.dev.servlet.utils.EndpointParser;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class RequestValidator {

    public static void validate(EndpointParser endpoint, RequestMapping mapping, Request request) throws ServiceException {
        validateMethod(request.method(), mapping);
        validateAuth(request.token(), mapping);
        validadeRoles(mapping.roles(), request);
        validateApiVersion(endpoint.getApiVersion(), mapping);
        validateConstraints(mapping.validators(), request);
    }

    private static void validadeRoles(PerfilEnum[] roles, Request request) throws ServiceException {
        if (CollectionUtils.isEmpty(roles)) {
            return;
        }

        User user = CryptoUtils.getUser(request.token());

        for (PerfilEnum role : roles) {
            if (!user.hasRole(role)) {
                throw ServiceException.badRequest("User does not have permission to access this endpoint");
            }
        }
    }

    private static void validateApiVersion(String apiVersion, RequestMapping mapping) throws ServiceException {
        if (!mapping.apiVersion().equals(apiVersion)) {
            throw ServiceException.badRequest("API Not implemented");
        }
    }

    private static void validateConstraints(Validator[] validators, Request request) throws ServiceException {
        List<String> resultErrors = new ArrayList<>();

        for (Validator validator : validators) {
            for (String value : validator.values()) {
                String parameterValue = request.getParameter(value);

                List<String> errors = new ConstraintValidator(validator.constraints()).validate(parameterValue);
                if (!CollectionUtils.isEmpty(errors)) {
                    resultErrors.addAll(errors);
                }
            }
        }

        if (!CollectionUtils.isEmpty(resultErrors)) {
            throw ServiceException.badRequest(String.join("\n", resultErrors));
        }
    }

    private static void validateMethod(String method, RequestMapping mapping) throws ServiceException {
        if (!mapping.method().getMethod().equals(method)) {
            throw ServiceException.badRequest("Method not allowed. Expected: " + mapping.method() + ", but got: " + method);
        }
    }

    private static void validateAuth(String token, RequestMapping mapping) throws ServiceException {
        if (mapping.requestAuth() && (token == null || token.isEmpty())) {
            throw new ServiceException(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required.");
        }
    }
}