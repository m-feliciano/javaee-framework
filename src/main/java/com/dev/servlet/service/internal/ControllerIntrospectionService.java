package com.dev.servlet.service.internal;

import com.dev.servlet.core.annotation.Authorization;
import com.dev.servlet.core.annotation.Controller;
import com.dev.servlet.core.annotation.Property;
import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.util.ClassUtil;
import com.dev.servlet.domain.model.enums.RoleType;
import com.dev.servlet.service.internal.inspector.ControllerInfo;
import com.dev.servlet.service.internal.inspector.MethodInfo;
import com.dev.servlet.service.internal.inspector.ParamInfo;
import jakarta.inject.Singleton;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ControllerIntrospectionService {

    private static final String CONTROLLERS_PACKAGE = "com.dev.servlet.controller";

    public List<ControllerInfo> listControllers() {
        try {
            List<Class<?>> classes = ClassUtil.scanPackage(CONTROLLERS_PACKAGE, Controller.class);

            return classes.stream()
                    .map(clz -> {
                        String base = clz.getAnnotation(Controller.class).value();
                        List<MethodInfo> methods = buildMethodInfos(clz);
                        return new ControllerInfo(clz.getSimpleName(), base, methods);
                    })
                    .parallel()
                    .toList();

        } catch (Exception e) {
            throw new RuntimeException("Failed to inspect controllers", e);
        }
    }

    private List<MethodInfo> buildMethodInfos(Class<?> clazz) {
        List<MethodInfo> methods = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(RequestMapping.class)) continue;
            RequestMapping rm = method.getAnnotation(RequestMapping.class);

            List<ParamInfo> params = buildParamInfos(method);
            List<String> roles = extractRoles(rm);

            String returnType = formatReturnType(method.getGenericReturnType().getTypeName());

            String jsonType = rm.jsonType() != null && rm.jsonType() != Void.class ? rm.jsonType().getSimpleName() : "void";
            String path = rm.value();
            String httpMethod = rm.method().name();
            boolean requestAuth = rm.requestAuth();

            methods.add(new MethodInfo(path, httpMethod, jsonType, requestAuth, roles, params, returnType));
        }

        return methods;
    }

    private List<ParamInfo> buildParamInfos(Method method) {
        List<ParamInfo> params = new ArrayList<>();
        for (Parameter p : method.getParameters()) {
            Property prop = p.getAnnotation(Property.class);
            Authorization auth = p.getAnnotation(Authorization.class);

            String propName = prop != null ? prop.value() : null;
            String typed;
            if (auth != null) {
                typed = "Authorization";
            } else {
                typed = p.getParameterizedType().getTypeName();
                typed = typed.substring(typed.lastIndexOf(".") + 1);
            }

            params.add(new ParamInfo(typed, propName));
        }
        return params;
    }

    private List<String> extractRoles(RequestMapping rm) {
        List<String> roles = new ArrayList<>();

        if (rm.roles().length == 0) {
            roles.add(RoleType.DEFAULT.name());
        } else {
            for (RoleType r : rm.roles())
                roles.add(r.name());
        }

        return roles;
    }

    private String formatReturnType(String returnType) {
        if (returnType.contains("HttpResponse")) {
            // Unwrap HttpResponse<>
            int indexOf = returnType.lastIndexOf(">");
            returnType = returnType.substring(returnType.indexOf("<") + 1, indexOf);
        }

        if (returnType.contains("<")) {
            var realType = returnType.substring(0, returnType.indexOf("<"));
            realType = realType.substring(realType.lastIndexOf(".") + 1);
            var genericType = returnType.substring(returnType.indexOf("<") + 1, returnType.lastIndexOf(">"));
            return realType + "<" + genericType.substring(genericType.lastIndexOf(".") + 1) + ">";
        }

        return returnType.substring(returnType.lastIndexOf(".") + 1);
    }
}
