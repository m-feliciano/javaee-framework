package com.dev.servlet.adapter.in.web.validator.internal;

import com.dev.servlet.adapter.in.web.annotation.RequestMapping;
import com.dev.servlet.adapter.in.web.dto.Request;
import com.dev.servlet.adapter.in.web.validator.ValidationHandler;
import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.shared.util.CloneUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.stream.Collectors;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

public class ConstraintValidationHandler implements ValidationHandler {
    private static final ValidatorFactory factory = jakarta.validation.Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    public void validate(RequestMapping mapping, Request request) throws AppException {
        if (mapping.jsonType() == Void.class || request.getPayload() == null) {
            return;
        }

        Object payload = CloneUtil.fromJson(request.getPayload(), mapping.jsonType());
        var violations = validator.validate(payload);
        if (violations.isEmpty()) return;

        String errors = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "))
                .trim();

        throw new AppException(SC_BAD_REQUEST, errors);
    }
}
