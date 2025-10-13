package com.dev.servlet.controller.base;

import com.dev.servlet.core.annotation.Authentication;
import com.dev.servlet.core.annotation.Property;
import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.core.util.EndpointParser;
import com.dev.servlet.core.util.PropertiesUtil;
import com.dev.servlet.core.validator.RequestValidator;
import com.dev.servlet.domain.transfer.Request;
import com.dev.servlet.domain.transfer.records.Query;
import com.dev.servlet.infrastructure.persistence.IPageRequest;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.Set;

import static com.dev.servlet.core.util.ThrowableUtils.internalServerError;

public abstract class BaseRouterController {
    private final Set<Method> reflections = new HashSet<>();

    protected BaseRouterController() {
        initRouteMapping();
    }

    private void initRouteMapping() {
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(RequestMapping.class)) {
                reflections.add(method);
            }
        }
    }

    private Object[] prepareMethodArguments(Method method, Request request) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            args[i] = resolveArgument(parameters[i], request);
        }
        return args;
    }

    public <U> IHttpResponse<U> route(EndpointParser endpoint, Request request) throws Exception {
        var method = routeMappingFromEndpoint("/" + endpoint.getEndpoint());
        var requestMapping = method.getAnnotation(RequestMapping.class);
        RequestValidator.validate(endpoint, requestMapping, request);
        Object[] args = prepareMethodArguments(method, request);
        return invokeServiceMethod(this, method, args);
    }

    private Method routeMappingFromEndpoint(String endpoint) throws ServiceException {
        return reflections.stream()
                .filter(m -> m.getAnnotation(RequestMapping.class).value().equals(endpoint))
                .findFirst()
                .orElseThrow(() -> internalServerError("Resource not found: " + endpoint));
    }

    private Object resolveArgument(Parameter parameter, Request request) {
        if (Request.class.isAssignableFrom(parameter.getType())) {
            return request;
        }
        if (Query.class.isAssignableFrom(parameter.getType())) {
            return request.getQuery();
        }
        if (IPageRequest.class.isAssignableFrom(parameter.getType())) {
            return request.getPageRequest();
        }

        if (parameter.isAnnotationPresent(Authentication.class)) {
            return request.getToken();
        }
        if (parameter.isAnnotationPresent(Property.class)) {
            String propertyKey = parameter.getAnnotation(Property.class).value();
            return PropertiesUtil.getProperty(propertyKey, "");
        }

        return request.getPayload(parameter.getType());
    }

    private <U> IHttpResponse<U> invokeServiceMethod(Object instance, Method method, Object[] args) throws Exception {
        @SuppressWarnings("ALL")
        var response = (IHttpResponse<U>) method.invoke(instance, args);
        return response;
    }
}
