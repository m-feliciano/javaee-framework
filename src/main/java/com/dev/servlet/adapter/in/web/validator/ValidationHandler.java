package com.dev.servlet.adapter.in.web.validator;

import com.dev.servlet.adapter.in.web.annotation.RequestMapping;
import com.dev.servlet.adapter.in.web.dto.Request;
import com.dev.servlet.application.exception.AppException;

public interface ValidationHandler {
    void validate(RequestMapping mapping, Request request) throws AppException;
}
