package com.dev.servlet.core.validator;

import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.domain.transfer.Request;

public interface ValidationHandler {

    void validate(RequestMapping mapping, Request request) throws ServiceException;
}
