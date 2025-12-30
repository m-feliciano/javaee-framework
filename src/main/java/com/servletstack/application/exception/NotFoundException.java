package com.servletstack.application.exception;

public class NotFoundException extends AppException {
    public NotFoundException() {
        super(404, "Resource not found");
    }
}
