package com.dev.servlet.application.exception;

public class NotFoundException extends AppException {
    public NotFoundException() {
        super(404, "Resource not found");
    }
}
