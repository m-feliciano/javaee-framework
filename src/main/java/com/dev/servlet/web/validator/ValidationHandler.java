package com.dev.servlet.web.validator;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.web.Request;
import com.dev.servlet.web.annotation.RequestMapping;

public interface ValidationHandler {
    void validate(RequestMapping mapping, Request request) throws ApplicationException;
}
