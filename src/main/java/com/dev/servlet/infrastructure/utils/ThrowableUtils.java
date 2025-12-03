package com.dev.servlet.infrastructure.utils;

import com.dev.servlet.application.exception.ApplicationException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class ThrowableUtils {

    public static ApplicationException serviceError(int statusCode, String message) {
        return ApplicationException.builder().code(statusCode).message(message).build();
    }

    public static ApplicationException internalServerError(String message) {
        return ApplicationException.builder().code(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).message(message).build();
    }

    public static ApplicationException notFound(String message) {
        return ApplicationException.builder().code(HttpServletResponse.SC_NOT_FOUND).message(message).build();
    }
}
