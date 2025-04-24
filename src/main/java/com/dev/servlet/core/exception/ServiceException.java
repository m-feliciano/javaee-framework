package com.dev.servlet.core.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@Builder
@AllArgsConstructor
public class ServiceException extends Exception {
    private final int code;
    private final String message;

    public ServiceException(String message) {
        this(500, message);
        log.error(message);
    }
}
