package com.dev.servlet.controller.base;

import com.dev.servlet.core.annotation.Authorization;
import com.dev.servlet.core.annotation.Property;
import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.core.util.CloneUtil;
import com.dev.servlet.core.util.EndpointParser;
import com.dev.servlet.core.util.JwtUtil;
import com.dev.servlet.core.util.Properties;
import com.dev.servlet.core.validator.RequestValidator;
import com.dev.servlet.domain.records.Query;
import com.dev.servlet.domain.request.Request;
import com.dev.servlet.infrastructure.persistence.IPageRequest;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Set;

import static com.dev.servlet.core.util.ClassUtil.findMethodsOnInterfaceRecursive;
import static com.dev.servlet.core.util.ThrowableUtils.internalServerError;

public abstract class BaseRouterController {

    private final static Map<String, Set<Method>> reflections = new java.util.concurrent.ConcurrentHashMap<>();
    protected JwtUtil jwts;

    protected BaseRouterController() {
        initRouteMapping();
    }

    private void initRouteMapping() {
        reflections.computeIfAbsent(this.getClass().getName(), k ->
                Set.copyOf(findMethodsOnInterfaceRecursive(this.getClass())));
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
        var method = routeMappingFromEndpoint("/" + endpoint.path());
        var requestMapping = method.getAnnotation(RequestMapping.class);

        RequestValidator validator = new RequestValidator(endpoint, jwts);
        validator.validate(requestMapping, request);

        Object[] args = prepareMethodArguments(method, request);
        return invokeServiceMethod(this, method, args);
    }

    private Method routeMappingFromEndpoint(String endpoint) throws ServiceException {
        Set<Method> methods = reflections.get(this.getClass().getName());
        for (Method method : methods) {
            RequestMapping mapping = method.getAnnotation(RequestMapping.class);
            if (mapping.value().equals(endpoint)) {
                return method;
            }
        }

        throw internalServerError("Endpoint not implemented: " + endpoint);
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

        if (parameter.isAnnotationPresent(Authorization.class)) {
            return request.getToken();
        }
        if (parameter.isAnnotationPresent(Property.class)) {
            String propertyKey = parameter.getAnnotation(Property.class).value();
            return Properties.get(propertyKey);
        }

        return CloneUtil.fromJson(request.getJsonBody(), parameter.getType());
    }

    private <U> IHttpResponse<U> invokeServiceMethod(Object instance, Method method, Object[] args) throws Exception {
        @SuppressWarnings("ALL")
        var response = (IHttpResponse<U>) method.invoke(instance, args);
        return response;
    }
}
