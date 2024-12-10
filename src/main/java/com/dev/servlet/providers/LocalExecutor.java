package com.dev.servlet.providers;

import com.dev.servlet.dto.ServiceException;
import com.dev.servlet.interfaces.IHttpExecutor;
import com.dev.servlet.interfaces.IHttpResponse;
import com.dev.servlet.interfaces.RequestMapping;
import com.dev.servlet.pojo.records.HttpResponse;
import com.dev.servlet.pojo.records.Request;
import com.dev.servlet.utils.BeanUtil;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

/**
 * This class is responsible for executing the HTTP request.
 *
 * @author marcelo.feliciano
 * @since 1.0.0
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class LocalExecutor<J> implements IHttpExecutor<J> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalExecutor.class);
    private static final String ERROR_CHECK_YOUR_URL = "Check your URL.";

    public static <J> LocalExecutor<J> newInstance() {
        return new LocalExecutor<>();
    }

    /**
     * Calls the appropriate service method based on the request.
     *
     * @return {@linkplain HttpResponse}
     * @author marcelo.feliciano
     */
    public IHttpResponse<J> send(Request request) {
        IHttpResponse<J> response;

        try {
            // /category/new -> [category, new]
            String[] parts = parserEndpoint(request);

            String path = "/".concat(parts[0]);
            Object instance = this.newInstance(path);
            if (instance == null) {
                throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, ERROR_CHECK_YOUR_URL);
            }

            String endpoint = parts.length > 1 ? "/".concat(parts[1]) : "/";

            Method method = getServiceMethod(endpoint, instance);
            if (method == null) {
                throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, ERROR_CHECK_YOUR_URL);
            }

            this.validateRequestMethod(request, method);

            Object[] args = {request}; // Can be extended to include more arguments as needed
            response = invokeMethod(instance, method, args);

        } catch (Exception exception) {
            return newHttpResponseError(exception);
        }

        return response;
    }

    /**
     * Handles the generic exception.
     *
     * @param exception {@linkplain Exception} the exception to handle
     * @return {@linkplain IHttpResponse} with the error message
     */
    private <U> IHttpResponse<U> newHttpResponseError(Exception exception) {
        String message = exception.getMessage();
        int code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

        if (exception instanceof ServiceException || exception.getCause() instanceof ServiceException) {
            var serviceException = (ServiceException) (exception instanceof ServiceException ? exception : exception.getCause());
            code = serviceException.getCode();
            message = serviceException.getMessage();
        } else {
            LOGGER.error("An error occurred while processing the request", exception);
        }

        return HttpResponse.ofError(code, message);
    }

    /**
     * Parses the endpoint to get the service and action.
     *
     * @param request the request to parse
     * @return {@linkplain String[]}
     */
    private String[] parserEndpoint(Request request) {
        return Arrays.stream(request.getEndpoint().split("/"))
                .skip(1)
                .toArray(String[]::new);
    }

    /**
     * Validates the request method.
     *
     * @param request {@linkplain Request}
     * @param method  {@linkplain Method}
     */
    private void validateRequestMethod(Request request, Method method) throws ServiceException {
        RequestMapping mapping = method.getDeclaredAnnotation(RequestMapping.class);

        if (!mapping.method().equals(request.getMethod())) {
            throw new ServiceException(400, "Method not allowed. Expected: " + mapping.method() + " but got: " + request.getMethod());
        }
    }

    /**
     * Invokes the method in the given instance.
     *
     * @param instance {@linkplain Object}
     * @param method   {@linkplain Method}
     * @param args     {@linkplain Object} the arguments to pass to the method
     * @return the result of the method invocation
     * @throws Exception if an error occurs during the method invocation
     */
    @SuppressWarnings("unchecked")
    private static <U> IHttpResponse<U> invokeMethod(Object instance, Method method, Object[] args) throws
            Exception {
        IHttpResponse<U> response;
        response = (IHttpResponse<U>) invokeActionMethod(instance, method, args);
        return response;
    }

    private Object newInstance(String service) {
        // Get the service instance from the dependency resolver
        var resolver = BeanUtil.getResolver();
        return resolver.getService(service);
    }

    /**
     * Finds the method corresponding to the action in the given object.
     *
     * @param instance {@linkplain Object}
     * @param method   {@linkplain Method}
     * @param params   {@linkplain Object[]} the arguments to pass to the method
     * @return the result of the method invocation
     * @author marcelo.feliciano
     */
    private static Object invokeActionMethod(Object instance, Method method, Object[] params) throws Exception {
        Object[] args = new Object[method.getParameters().length];

        for (int i = 0; i < args.length; i++) {
            Parameter parameter = method.getParameters()[i];
            args[i] = Arrays.stream(params)
                    .filter(d -> d != null && d.getClass().equals(parameter.getType()))
                    .map(parameter.getType()::cast)
                    .findFirst()
                    .orElse(null);
        }

        return method.invoke(instance, args);
    }

    /**
     * Finds the method corresponding to the action in the given object.
     *
     * @param serviceName as defined in the ResourceMapping annotation
     * @param object      an instance of the object
     * @return the method corresponding or null
     * @author marcelo.feliciano
     */
    private static Method getServiceMethod(String serviceName, Object object) {
        List<Method> methods = MethodUtils.getMethodsListWithAnnotation(object.getClass(), RequestMapping.class);

        return methods.stream()
                .filter(m -> m.getAnnotation(RequestMapping.class).value().equals(serviceName))
                .findFirst()
                .orElse(null);
    }
}