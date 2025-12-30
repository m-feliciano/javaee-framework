package com.servletstack.adapter.in.web.controller.internal.base;

import com.servletstack.adapter.in.web.annotation.Async;
import com.servletstack.adapter.in.web.annotation.Authorization;
import com.servletstack.adapter.in.web.annotation.Cache;
import com.servletstack.adapter.in.web.annotation.Property;
import com.servletstack.adapter.in.web.annotation.RequestMapping;
import com.servletstack.adapter.in.web.dto.HttpResponse;
import com.servletstack.adapter.in.web.dto.IHttpResponse;
import com.servletstack.adapter.in.web.dto.Request;
import com.servletstack.adapter.in.web.util.EndpointParser;
import com.servletstack.adapter.in.web.validator.RequestValidator;
import com.servletstack.application.exception.AppException;
import com.servletstack.application.port.out.cache.CachePort;
import com.servletstack.application.port.out.security.AuthenticationPort;
import com.servletstack.infrastructure.config.Properties;
import com.servletstack.infrastructure.persistence.transfer.IPageRequest;
import com.servletstack.infrastructure.utils.URIUtils;
import com.servletstack.shared.util.CloneUtil;
import com.servletstack.shared.vo.Query;
import jakarta.enterprise.context.control.RequestContextController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static com.servletstack.shared.util.ClassUtil.findMethodsOnInterfaceRecursive;
import static com.servletstack.shared.util.ClassUtil.findMethodsRecursive;
import static jakarta.servlet.http.HttpServletResponse.SC_ACCEPTED;

@Slf4j
public abstract class BaseRouterController {
    private static final AtomicLong counter = new AtomicLong(0);
    private static final Map<String, Set<MethodMapping>> reflections = new ConcurrentHashMap<>();

    protected CachePort cache;
    protected AuthenticationPort auth;
    protected RequestContextController context;

    protected BaseRouterController() {
        initRouteMapping();
    }

    private static String composeNamespace(EndpointParser endpoint, String suffix) {
        return endpoint.path()
                       .replace("{", "")
                       .replace("}", "")
                       .replace("/", "_") + ":" + suffix;
    }

    // Prevents refence injection issues
    protected abstract Class<? extends BaseRouterController> implementation();

    public <U> IHttpResponse<U> route(EndpointParser endpoint, Request request) throws Exception {
        log.trace("Route endpoint: {} request: {}", endpoint, request);

        final String path = endpoint.path();
        final MethodMapping methodMapping = routeMappingFromEndpoint("/" + path);
        final RequestMapping requestMapping = methodMapping.parent();

        log.debug("Validating request for endpoint: {}", path);

        RequestValidator validator = new RequestValidator(endpoint, auth);
        validator.validate(requestMapping, request);

        final Method method = methodMapping.implementation();
        log.debug("Preparing method arguments for endpoint: {}", path);

        Object[] args = prepareMethodArguments(method, request);
        log.debug("Routing to implementation: {} for endpoint: {}", method.getName(), path);

        final Cache cache = method.getAnnotation(Cache.class);
        if (request.getToken() != null && cache != null && StringUtils.isNotBlank(cache.value())) {
            UUID userId = auth.extractUserId(request.getToken());
            return executeCachedHttp(endpoint, method, userId, args);
        }

        boolean async = method.isAnnotationPresent(Async.class);

        IHttpResponse<U> response = executeHttp(path, async, method, args);
        invalidateCaches(request.getToken(), cache);
        return response;
    }

    private void initRouteMapping() {
        var clazz = implementation();
        reflections.computeIfAbsent(clazz.getName(), k -> {
            log.debug("Initializing route mappings for controller: {}", clazz.getName());

            List<Method> implementationMethods = findMethodsRecursive(clazz);

            Set<MethodMapping> mappings = new HashSet<>();
            for (Method in : findMethodsOnInterfaceRecursive(clazz)) {
                log.debug("Discovered route method: {} in controller interface: {}", in.getName(), clazz.getName());

                for (Method imp : implementationMethods) {
                    if (imp.getName().equals(in.getName())
                        && imp.getParameterCount() == in.getParameterCount()) {
                        var mapping = in.getAnnotation(RequestMapping.class);
                        log.debug("Mapping endpoint: {} to method: {}", mapping.value(), in.getName());
                        mappings.add(new MethodMapping(mapping, imp));
                        break;
                    }
                }
            }

            return mappings;
        });
    }

    @SuppressWarnings("unchecked")
    private <U> IHttpResponse<U> invokeServiceMethod(Method method, Object[] args) throws Exception {
        return (IHttpResponse<U>) method.invoke(this, args);
    }

    private MethodMapping routeMappingFromEndpoint(String endpoint) throws AppException {
        String controllerName = implementation().getName();
        Set<MethodMapping> methodMappings = reflections.get(controllerName);
        if (methodMappings == null) {
            throw new AppException("No route mappings initialized for controller: " + controllerName);
        }

        for (MethodMapping mm : methodMappings) {
            if (mm.parent().value().equals(endpoint)) {
                return mm;
            }
        }

        throw new AppException("Api not implemented!");
    }

    private Object[] prepareMethodArguments(Method method, Request request) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            args[i] = resolveArgument(parameters[i], request);
        }
        return args;
    }

    private Object resolveArgument(Parameter parameter, Request request) {
        if (Request.class.isAssignableFrom(parameter.getType())) {
            return request;
        }

        if (Query.class.isAssignableFrom(parameter.getType())) {
            return request.getQuery();
        }

        if (IPageRequest.class.isAssignableFrom(parameter.getType())) {
            return request.getQuery() == null
                    ? URIUtils.buildPageRequest()
                    : URIUtils.getPageRequest(request.getQuery());
        }

        if (parameter.isAnnotationPresent(Authorization.class)) {
            return request.getToken();
        }

        if (parameter.isAnnotationPresent(Property.class)) {
            return Properties.get(parameter.getAnnotation(Property.class).value());
        }

        return CloneUtil.fromJson(request.getPayload(), parameter.getType());
    }

    private <U> IHttpResponse<U> executeHttp(String endpoint,
                                             boolean async,
                                             Method method,
                                             Object[] args) throws Exception {

        if (!async) {
            log.debug("Executing synchronous HTTP for method endpoint: {}", endpoint);
            return invokeServiceMethod(method, args);
        }

        log.debug("Executing asynchronous HTTP for method endpoint: {}", endpoint);

        String threadName = endpoint
                .replace("/", "_")
                .replace("{", "")
                .replace("}", "");

        Thread.ofVirtual()
                .name("async-request-%s-%d".formatted(threadName, counter.incrementAndGet()))
                .start(() -> {
                    context.activate();
                    try {
                        invokeServiceMethod(method, args);
                    } catch (Exception e) {
                        log.error("Error during async request", e);
                    } finally {
                        context.deactivate();
                    }
                });

        return HttpResponse.<U>newBuilder()
                .statusCode(SC_ACCEPTED)
                .build();
    }

    private <U> IHttpResponse<U> executeCachedHttp(EndpointParser parser,
                                                   Method method,
                                                   UUID uuid,
                                                   Object[] args) throws Exception {
        log.debug("Executing cached HTTP for method endpoint: {}", parser.path());

        Cache cache = method.getAnnotation(Cache.class);
        boolean async = method.isAnnotationPresent(Async.class);
        String namespace = composeNamespace(parser, cache.value());

        IHttpResponse<U> response = this.cache.get(namespace, uuid);
        if (response == null) {
            response = executeHttp(parser.path(), async, method, args);

            if (response.statusCode() >= 200 && response.statusCode() < 400) {
                Duration ttl = Duration.of(cache.duration(), cache.timeUnit().toChronoUnit());
                this.cache.set(namespace, uuid, response, ttl);
            }
        }

        return response;
    }

    private void invalidateCaches(String token, Cache cache) {
        if (cache == null || token == null) return;

        try {
            UUID userId = auth.extractUserId(token);
            for (String namespace : cache.invalidate()) {
                this.cache.clearSuffix(namespace, userId);
            }
        } catch (Exception ignored) {
        }
    }
}
