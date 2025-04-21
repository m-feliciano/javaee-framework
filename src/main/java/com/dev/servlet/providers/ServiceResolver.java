package com.dev.servlet.providers;

import com.dev.servlet.dto.ServiceException;
import com.dev.servlet.interfaces.RequestMapping;
import com.dev.servlet.utils.BeanUtil;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ServiceResolver {

    private static final ConcurrentHashMap<String, Method> methodCache = new ConcurrentHashMap<>();

    public static Object resolveServiceInstance(String serviceName) throws ServiceException {
        Object service = BeanUtil.getResolver().getService(serviceName);
        if (service == null) {
            throw ServiceException.badRequest("Check your URL.");
        }

        return service;
    }

    public static Method resolveServiceMethod(String serviceMethod, String apiVersion, Object serviceInstance) throws ServiceException {
        String cacheKey = serviceInstance.getClass().getName() + ":" + apiVersion + ":" + serviceMethod;

        Method cachedMethod = methodCache.computeIfAbsent(cacheKey, key -> {
            Method[] methods = serviceInstance.getClass().getMethods();

            for (Method method : methods) {
                if (!method.isAnnotationPresent(RequestMapping.class)) {
                    continue;
                }

                RequestMapping mapping = method.getAnnotation(RequestMapping.class);
                if (mapping.value().equals("/" + serviceMethod) && mapping.apiVersion().equals(apiVersion)) {
                    return method;
                }
            }

            return null;
        });

        if (cachedMethod == null) {
            throw ServiceException.badRequest("Check your URL.");
        }

        return cachedMethod;
    }
}