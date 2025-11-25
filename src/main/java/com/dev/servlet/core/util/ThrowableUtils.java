package com.dev.servlet.core.util;
import com.dev.servlet.core.exception.ServiceException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class ThrowableUtils {

    public static ServiceException serviceError(int statusCode, String message) {
        return ServiceException.builder().code(statusCode).message(message).build();
    }

    public static ServiceException internalServerError(String message) {
        return ServiceException.builder().code(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).message(message).build();
    }

    public static ServiceException notFound(String message) {
        return ServiceException.builder().code(HttpServletResponse.SC_NOT_FOUND).message(message).build();
    }
}
