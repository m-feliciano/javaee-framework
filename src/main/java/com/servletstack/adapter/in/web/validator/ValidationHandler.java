package com.servletstack.adapter.in.web.validator;

import com.servletstack.adapter.in.web.annotation.RequestMapping;
import com.servletstack.adapter.in.web.dto.Request;
import com.servletstack.application.exception.AppException;

public interface ValidationHandler {
    void validate(RequestMapping mapping, Request request) throws AppException;
}
