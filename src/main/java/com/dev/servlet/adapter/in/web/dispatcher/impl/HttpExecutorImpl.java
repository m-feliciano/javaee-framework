package com.dev.servlet.adapter.in.web.dispatcher.impl;

import com.dev.servlet.adapter.in.web.controller.internal.base.BaseRouterController;
import com.dev.servlet.adapter.in.web.dispatcher.HttpExecutor;
import com.dev.servlet.adapter.in.web.dto.HttpResponse;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.adapter.in.web.dto.Request;
import com.dev.servlet.adapter.in.web.util.EndpointParser;
import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.infrastructure.utils.BeanUtil;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Objects;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

@Slf4j
@NoArgsConstructor
@ApplicationScoped
public class HttpExecutorImpl implements HttpExecutor {

    private static BaseRouterController resolveController(EndpointParser parser) throws AppException {
        try {
            var routerController = (BaseRouterController) BeanUtil.getResolver().getBean(parser.controller() + "Controller");
            Objects.requireNonNull(routerController);
            return routerController;
        } catch (Exception e) {
            throw new AppException(SC_BAD_REQUEST,
                    "Error resolving service endpoint: %s%s".formatted(parser.controller(), parser.path()));
        }
    }

    @Override
    public IHttpResponse<?> send(Request request) {
        final String endpoint = request.getEndpoint();
        int maxRetries = ObjectUtils.getIfNull(request.getRetry(), 0);

        IHttpResponse<?> response;
        try {
            EndpointParser parser = EndpointParser.of(endpoint);
            BaseRouterController router = resolveController(parser);

            do {
                response = router.route(parser, request);

                if (response.statusCode() >= 200 && response.statusCode() < 500) return response;

                if (maxRetries <= 0) return response;

                waitBeforeRetry(maxRetries);
            } while (--maxRetries > 0);

            return response;
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private void waitBeforeRetry(int attempt) {
        try {
            long waitTime = (long) Math.pow(2, attempt) * 100;
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Retry interrupted");
        }
    }

    private IHttpResponse<?> handleException(Exception ex) {
        log.error("HttpExecutor: Unhandled exception occurred: {}", ex.getMessage(), ex);

        int code = 500;
        String message = "An unexpected error occurred.";
        if (ex instanceof AppException exception) {
            code = exception.getCode();
            message = exception.getMessage();
        } else if (ex.getCause() instanceof AppException cause) {
            code = cause.getCode();
            message = cause.getMessage();
        }

        return HttpResponse.error(code, message);
    }
}
