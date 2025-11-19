package com.dev.servlet.core.validator.internal;

import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.util.CloneUtil;
import com.dev.servlet.core.validator.ValidationHandler;
import com.dev.servlet.domain.request.Request;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;

public class ConstraintValidationHandler implements ValidationHandler {

    private static final ValidatorFactory factory = javax.validation.Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    public void validate(RequestMapping mapping, Request request) throws ServiceException {
        if (mapping.jsonType() == Void.class) return;

        Object payload = CloneUtil.fromJson(request.getJsonBody(), mapping.jsonType());

        Set<ConstraintViolation<Object>> violations = validator.validate(payload);
        if (violations.isEmpty()) return;

        String errors = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "))
                .trim();

        throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, errors);
    }
}
