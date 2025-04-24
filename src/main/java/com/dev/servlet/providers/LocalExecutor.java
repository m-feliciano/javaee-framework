package com.dev.servlet.providers;

import com.dev.servlet.controllers.router.BaseRouterController;
import com.dev.servlet.dto.ServiceException;
import com.dev.servlet.interfaces.IHttpExecutor;
import com.dev.servlet.interfaces.IHttpResponse;
import com.dev.servlet.pojo.records.HttpResponse;
import com.dev.servlet.pojo.records.Request;
import com.dev.servlet.utils.BeanUtil;
import com.dev.servlet.utils.EndpointParser;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;

/**
 * This class is responsible for executing the HTTP request.
 *
 * @author marcelo.feliciano
 * @since 1.0.0
 */
@Slf4j
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class LocalExecutor<J> implements IHttpExecutor<J> {

    public static <J> LocalExecutor<J> newInstance() {
        return new LocalExecutor<>();
    }

    /**
     * Executes the HTTP request.
     *
     * @param request {@linkplain Request}
     * @return {@linkplain IHttpResponse}
     */
    @Override
    @SuppressWarnings("unchecked")
    public IHttpResponse<J> send(Request request) {
        try {
            var parser = new EndpointParser(request);

            BaseRouterController routerController = resolveController(parser);
            return (IHttpResponse<J>) routerController.route(parser, request);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    /**
     * Resolves the controller instance based on the request path.
     *
     * @param endpoint The HTTP request
     * @return The controller instance
     */
    private BaseRouterController resolveController(EndpointParser endpoint) throws ServiceException {
        try {
            return (BaseRouterController) BeanUtil.getResolver().getService(endpoint.getService());
        } catch (Exception e) {
            throw ServiceException.badRequest("Error resolving service method for path: " + endpoint.getService());
        }
    }

    /**
     * Handles exceptions that occur during request processing.
     *
     * @param srcException The exception
     * @return The HTTP response with error details
     */
    private <U> IHttpResponse<U> handleException(Exception srcException) {
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

        return HttpResponse.ofError(code, message);
    }
}