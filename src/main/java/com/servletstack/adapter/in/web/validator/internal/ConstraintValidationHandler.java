package com.servletstack.adapter.in.web.validator.internal;

import com.servletstack.adapter.in.web.annotation.RequestMapping;
import com.servletstack.adapter.in.web.dto.Request;
import com.servletstack.adapter.in.web.validator.ValidationHandler;
import com.servletstack.application.exception.AppException;
import com.servletstack.shared.util.CloneUtil;
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
