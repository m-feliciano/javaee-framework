package com.dev.servlet.adapter.internal;

import com.dev.servlet.adapter.IHttpExecutor;
import com.dev.servlet.controller.base.BaseRouterController;
import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.response.HttpResponse;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.core.util.BeanUtil;
import com.dev.servlet.core.util.EndpointParser;
import com.dev.servlet.domain.transfer.Request;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Slf4j
@NoArgsConstructor
public class HttpExecutor<TResponse> implements IHttpExecutor<TResponse> {

    private static EndpointParser resolveEndpoint(String endpoint) throws ServiceException {
        try {
            return EndpointParser.of(endpoint);
        } catch (Exception e) {
            log.error("❌ Invalid endpoint [endpoint={}] - {}", endpoint, e.getMessage());
            throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, "Invalid endpoint: " + endpoint);
        }
    }

    @Override
    public IHttpResponse<TResponse> call(Request request) {
        final String endpoint = request.getEndpoint();
        int maxRetries = request.getRetry();
        int currentAttempt = 1;

        try {
            IHttpResponse<TResponse> response;
            EndpointParser parser = resolveEndpoint(endpoint);
            BaseRouterController router = resolveController(parser);

            do {
                log.debug("🔄 Executing request [endpoint={}, attempt={}/{}]", endpoint, currentAttempt, maxRetries + 1);

                response = router.route(parser, request);

                if (response.statusCode() >= 200 && response.statusCode() < 400) {
                    log.info("✅ Request successful [endpoint={}, status={}, attempt={}]", endpoint, response.statusCode(), currentAttempt);
                    return response;
                }

                if (response.statusCode() >= 400 && response.statusCode() < 500) {
                    log.warn("⚠️ Client error [endpoint={}, status={}, error={}]", endpoint, response.statusCode(), response.error());
                    return response;
                }

                log.error("❌ Server error [endpoint={}, status={}, error={}, attempt={}/{}]", endpoint, response.statusCode(), response.error(), currentAttempt, maxRetries + 1);

                if (maxRetries <= 0) {
                    log.warn("🚫 Max retries exhausted [endpoint={}]", endpoint);
                    return response;
                }

                long waitTime = waitBeforeRetry(maxRetries);
                log.debug("⏳ Retrying in {}ms [endpoint={}, attempt={}/{}]", waitTime, endpoint, currentAttempt + 1, maxRetries + 1);

                currentAttempt++;
            } while (--maxRetries > 0);

            return response;
        } catch (Exception e) {
            log.error("💥 Unexpected error [endpoint={}]", endpoint, e);
            return handleException(e);
        }
    }

    private static BaseRouterController resolveController(EndpointParser parser) throws ServiceException {
        try {
            BaseRouterController controller = (BaseRouterController) BeanUtil.getResolver().getBean(parser.controller() + "Controller");
            Objects.requireNonNull(controller);
            return controller;
        } catch (Exception e) {
            log.error("🔌 Controller not found [endpoint={}, controller={}] - {}",
                    parser.path(), parser.controller(), e.getMessage());
            throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, "Error resolving service endpoint: " + parser.path());
        }
    }

    private long waitBeforeRetry(int attempt) {
        try {
            long waitTime = (long) Math.pow(2, attempt) * 100;
            Thread.sleep(waitTime);
            return waitTime;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("⏸️ Retry interrupted");
            return 0;
        }
    }

    private IHttpResponse<TResponse> handleException(Exception srcException) {
        int code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        String message = "An unexpected error occurred.";

        if (srcException instanceof ServiceException exception) {
            code = exception.getCode();
            message = exception.getMessage();
        } else if (srcException.getCause() instanceof ServiceException cause) {
            code = cause.getCode();
            message = cause.getMessage();
        } else {
            log.error("💥 Unhandled exception - {}", srcException.getMessage(), srcException);
        }

        return HttpResponse.error(code, message);
    }
}
