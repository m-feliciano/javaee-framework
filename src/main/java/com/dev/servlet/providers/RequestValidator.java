package com.dev.servlet.providers;

import com.dev.servlet.dto.ServiceException;
import com.dev.servlet.interfaces.Constraints;
import com.dev.servlet.interfaces.RequestMapping;
import com.dev.servlet.interfaces.Validator;
import com.dev.servlet.pojo.ConstraintValidator;
import com.dev.servlet.pojo.records.Request;
import com.dev.servlet.utils.CollectionUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestValidator {

    public static void validate(Request request, RequestMapping mapping) throws ServiceException {
        validateMethod(request, mapping);
        validateAuth(request, mapping);
        validateConstraints(request, mapping.validators());

    }

    private static void validateConstraints(Request request, Validator[] validators) throws ServiceException {
        List<String> errors = new ArrayList<>();

        for (Validator validator : validators) {
            for (String value : validator.values()) {
                String parameterValue = request.getParameter(value);

                List<String> result = validateConstraints(parameterValue, validator.constraints());
                if (!CollectionUtils.isEmpty(result)) {
                    errors.addAll(result);
                }
            }
        }

        if (!CollectionUtils.isEmpty(errors)) {
            throw ServiceException.badRequest(String.join("\n", errors));
        }
    }

    private static List<String> validateConstraints(String parameterValue, Constraints[] constraints) {
        var validator = new ConstraintValidator(constraints);
        return validator.validate(parameterValue);
    }

    private static void validateMethod(Request request, RequestMapping mapping) throws ServiceException {
        if (!mapping.method().getMethod().equals(request.method())) {
            throw ServiceException.badRequest("Method not allowed. Expected: " + mapping.method() + ", but got: " + request.method());
        }
    }

    private static void validateAuth(Request request, RequestMapping mapping) throws ServiceException {
        if (mapping.requestAuth() && request.token() == null) {
            throw new ServiceException(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required.");
        }
    }
}