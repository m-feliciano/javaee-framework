package com.dev.servlet.core.util;
import com.dev.servlet.core.exception.ServiceException;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class ThrowableUtils {
    public static void throwServiceError(int statusCode, String message) throws ServiceException {
        ServiceException.builder()
                .code(statusCode).message(message)
                .build()
                .throwError();
    }
}
