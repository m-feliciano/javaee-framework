package com.dev.servlet.web.dispatcher.impl;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.infrastructure.utils.BeanUtil;
import com.dev.servlet.shared.util.EndpointParser;
import com.dev.servlet.web.Request;
import com.dev.servlet.web.controller.internal.base.BaseRouterController;
import com.dev.servlet.web.dispatcher.HttpExecutor;
import com.dev.servlet.web.response.HttpResponse;
import com.dev.servlet.web.response.IHttpResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
@NoArgsConstructor
public class HttpExecutorImpl<J> implements HttpExecutor<J> {

    private static EndpointParser resolveEndpoint(String endpoint) throws ApplicationException {
        try {
            return EndpointParser.of(endpoint);
        } catch (Exception e) {
            throw new ApplicationException(HttpServletResponse.SC_BAD_REQUEST, "Invalid endpoint: " + endpoint);
        }
    }

    private static BaseRouterController resolveController(EndpointParser parser) throws ApplicationException {
        try {
            var routerController = (BaseRouterController) BeanUtil.getResolver().getBean(parser.controller() + "Controller");
            Objects.requireNonNull(routerController);
            return routerController;
        } catch (Exception e) {
            throw new ApplicationException(HttpServletResponse.SC_BAD_REQUEST, "Error resolving service endpoint: " + parser.path());
        }
    }

    @Override
    public IHttpResponse<J> send(Request request) {
        final String endpoint = request.getEndpoint();
        int maxRetries = request.getRetry();
        int currentAttempt = 1;
        try {
            IHttpResponse<J> response;
            EndpointParser parser = resolveEndpoint(endpoint);
            BaseRouterController router = resolveController(parser);
            do {
                response = router.route(parser, request);
                if (response.statusCode() >= 200 && response.statusCode() < 400) {
                    return response;
                }
                if (response.statusCode() >= 400 && response.statusCode() < 500) {
                    log.warn("HttpExecutor: client error [endpoint={}, status={}, error={}]", endpoint, response.statusCode(), response.error());
                    return response;
                }
                log.error("HttpExecutor: server error [endpoint={}, status={}, error={}, attempt={}/{}]", endpoint, response.statusCode(), response.error(), currentAttempt, maxRetries + 1);
                if (maxRetries <= 0) {
                    log.warn("HttpExecutor: max retries exhausted [endpoint={}]", endpoint);
                    return response;
                }
                long waitTime = waitBeforeRetry(maxRetries);
                currentAttempt++;
            } while (--maxRetries > 0);
            return response;
        } catch (Exception e) {
            log.error("HttpExecutor: unexpected error [endpoint={}]", endpoint, e);
            return handleException(e);
        }
    }

    private long waitBeforeRetry(int attempt) {
        try {
            long waitTime = (long) Math.pow(2, attempt) * 100;
            Thread.sleep(waitTime);
            return waitTime;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Retry interrupted");
            return 0;
        }
    }

    private IHttpResponse<J> handleException(Exception srcException) {
        int code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        String message = "An unexpected error occurred.";
        if (srcException instanceof ApplicationException exception) {
            code = exception.getCode();
            message = exception.getMessage();
        } else if (srcException.getCause() instanceof ApplicationException cause) {
            code = cause.getCode();
            message = cause.getMessage();
        } else {
            log.error("Ã°Å¸â€™Â¥ Unhandled exception - {}", srcException.getMessage(), srcException);
        }
        return HttpResponse.error(code, message);
    }
}
