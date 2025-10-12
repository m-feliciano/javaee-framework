package com.dev.servlet.adapter.internal;

import com.dev.servlet.adapter.IHttpExecutor;
import com.dev.servlet.controller.base.BaseRouterController;
import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.util.BeanUtil;
import com.dev.servlet.core.util.EndpointParser;
import com.dev.servlet.domain.transfer.Request;
import com.dev.servlet.core.response.HttpResponse;
import com.dev.servlet.core.response.IHttpResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Slf4j
@NoArgsConstructor
public class HttpExecutor<TResponse> implements IHttpExecutor<TResponse> {

    private static EndpointParser resolveEndpoint(String endpoint) throws ServiceException {
        try {
            return EndpointParser.of(endpoint);
        } catch (Exception e) {
            log.error("Error parsing endpoint: {}", endpoint, e);
            throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, "Invalid endpoint: " + endpoint);
        }
    }

    @Override
    public IHttpResponse<TResponse> call(Request request) {
        final String endpoint = request.getEndpoint();
        int maxRetries = request.getRetry();

        try {
            IHttpResponse<TResponse> response;
            EndpointParser parser = resolveEndpoint(endpoint);
            BaseRouterController router = resolveController(parser);

            do {
                log.debug("Executing request to {} (attempt {}/{})", endpoint, 1, maxRetries + 1);
                response = router.route(parser, request);
                if (response.statusCode() >= 200 && response.statusCode() < 400) {
                    return response;
                }
                if (response.statusCode() >= 400 && response.statusCode() < 500) {
                    log.warn("Client error for request to {}: {}", endpoint, response.error());
                    return response;
                }
                log.error("Request to {} returned errors: {}", endpoint, response.error());
                if (maxRetries <= 0) {
                    log.info("Maximum retries exhausted for endpoint {}", endpoint);
                    return response;
                }
                waitBeforeRetry(maxRetries);
                log.info("Retrying request to {} (attempt {}/{})", endpoint, 1, maxRetries + 1);
            } while (--maxRetries > 0);

            return response;
        } catch (Exception e) {
            log.error("Error processing request to {}: {}", endpoint, e.getMessage(), e);
            return handleException(e);
        }
    }

    private static BaseRouterController resolveController(EndpointParser parser) throws ServiceException {
        try {
            BaseRouterController controller = (BaseRouterController) BeanUtil.getResolver().getService(parser.getController());
            Objects.requireNonNull(controller);
            return controller;
        } catch (Exception e) {
            log.error("Error resolving service: {}", parser.getEndpoint(), e);
            throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, "Error resolving service endpoint: " + parser.getEndpoint());
        }
    }

    private void waitBeforeRetry(int attempt) {
        try {
            long waitTime = (long) Math.pow(2, attempt) * 100;
            Thread.sleep(waitTime);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
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
            log.error("An error occurred while processing the request", srcException);
        }
        return HttpResponse.error(code, message);
    }
}
