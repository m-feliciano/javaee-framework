package com.dev.servlet.application.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@Builder
@AllArgsConstructor
public class ApplicationException extends RuntimeException {
    private final int code;
    private final String message;

    public ApplicationException(String message) {
        this(500, message);
        log.error(message);
    }
}
