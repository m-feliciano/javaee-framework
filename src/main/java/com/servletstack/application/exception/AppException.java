package com.servletstack.application.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@Builder
@AllArgsConstructor
public class AppException extends RuntimeException {
    private final int code;
    private final String message;

    public AppException(String message) {
        this(500, message);
        log.error(message);
    }
}
