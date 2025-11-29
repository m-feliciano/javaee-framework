package com.dev.servlet.web.validator.internal;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.infrastructure.utils.CloneUtil;
import com.dev.servlet.web.Request;
import com.dev.servlet.web.annotation.RequestMapping;
import com.dev.servlet.web.validator.ValidationHandler;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;
import java.util.stream.Collectors;

public class ConstraintValidationHandler implements ValidationHandler {
    private static final ValidatorFactory factory = jakarta.validation.Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    public void validate(RequestMapping mapping, Request request) throws ApplicationException {
        if (mapping.jsonType() == Void.class) return;

        Object payload = CloneUtil.fromJson(request.getJsonBody(), mapping.jsonType());
        Set<ConstraintViolation<Object>> violations = validator.validate(payload);
        if (violations.isEmpty()) return;

        String errors = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "))
                .trim();

        throw new ApplicationException(HttpServletResponse.SC_BAD_REQUEST, errors);
    }
}
