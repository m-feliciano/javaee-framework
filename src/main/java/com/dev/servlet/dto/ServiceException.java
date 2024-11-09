package com.dev.servlet.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceException extends Exception {

    private static final Logger logger = LoggerFactory.getLogger(ServiceException.class);

    private final String message;
    private final int code;

    public ServiceException(Integer code, String message) {
        this.message = message;
        this.code = code;
        logger.error(message);
    }

    public ServiceException(String message) {
        this(500, message);
        logger.error(message);
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
