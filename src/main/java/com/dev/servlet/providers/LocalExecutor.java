package com.dev.servlet.providers;

import com.dev.servlet.dto.ServiceException;
import com.dev.servlet.interfaces.IHttpExecutor;
import com.dev.servlet.interfaces.IHttpResponse;
import com.dev.servlet.interfaces.RequestMapping;
import com.dev.servlet.pojo.records.HttpResponse;
import com.dev.servlet.pojo.records.Request;
import com.dev.servlet.utils.EndpointParser;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

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
    public IHttpResponse<J> send(Request request) {
        try {
            //Example: api/v1/<service>/<method>/<id|query>
            var parser = new EndpointParser(request);

            Object instance = ServiceResolver.resolveServiceInstance(parser.getService());
            Method method = ServiceResolver.resolveServiceMethod(
                    parser.getServiceName(), parser.getApiVersion(), instance);

            RequestValidator.validate(request, method.getAnnotation(RequestMapping.class));

            Object[] args = prepareMethodArguments(method, request);
            return invokeServiceMethod(instance, method, args);

        } catch (Exception e) {
            return handleException(e);
        }
    }

    /**
     * Prepares the method arguments based on the request parameters.
     *
     * @param method  The service method
     * @param request The HTTP request
     * @return Array of method arguments
     */
    private Object[] prepareMethodArguments(Method method, Request request) {
        return Arrays.stream(method.getParameters())
                .map(parameter -> resolveArgument(parameter, request))
                .toArray();
    }

    /**
     * Resolves the argument based on the parameter type.
     *
     * @param parameter The method parameter
     * @param request   The HTTP request
     * @return The resolved argument
     */
    private Object resolveArgument(Parameter parameter, Request request) {
        if (parameter.getType().isAssignableFrom(Request.class)) {
            return request;
        }

        if (request.body() != null) {
            return request.body()
                    .stream()
                    .filter(body -> body.getClass().isAssignableFrom(parameter.getType()))
                    .findFirst()
                    .orElse(null);
        }

        // Add more argument resolution logic if needed
        return null;
    }

    /**
     * Invokes the service method with the provided arguments.
     *
     * @param instance The service instance
     * @param method   The service method
     * @param args     The method arguments
     * @return The HTTP response
     * @throws Exception if an error occurs during invocation
     */
    private <U> IHttpResponse<U> invokeServiceMethod(Object instance, Method method, Object[] args) throws Exception {
        @SuppressWarnings("ALL")
        var response = (IHttpResponse<U>) method.invoke(instance, args);
        return response;
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