package com.dev.servlet.web.controller.internal.base;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.port.out.AuthenticationPort;
import com.dev.servlet.domain.valueobject.Query;
import com.dev.servlet.infrastructure.config.Properties;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.infrastructure.utils.CloneUtil;
import com.dev.servlet.shared.util.EndpointParser;
import com.dev.servlet.web.Request;
import com.dev.servlet.web.annotation.Authorization;
import com.dev.servlet.web.annotation.Property;
import com.dev.servlet.web.annotation.RequestMapping;
import com.dev.servlet.web.response.IHttpResponse;
import com.dev.servlet.web.validator.RequestValidator;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Set;

import static com.dev.servlet.infrastructure.utils.ClassUtil.findMethodsOnInterfaceRecursive;
import static com.dev.servlet.shared.util.ThrowableUtils.internalServerError;

public abstract class BaseRouterController {
    private final static Map<String, Set<Method>> reflections = new java.util.concurrent.ConcurrentHashMap<>();
    protected AuthenticationPort authenticationPort;

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

        RequestValidator validator = new RequestValidator(endpoint, authenticationPort);
        validator.validate(requestMapping, request);

        Object[] args = prepareMethodArguments(method, request);
        return invokeServiceMethod(this, method, args);
    }

    private Method routeMappingFromEndpoint(String endpoint) throws ApplicationException {
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
