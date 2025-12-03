package com.dev.servlet.infrastructure.utils;

import jakarta.enterprise.inject.spi.CDI;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class BeanUtil {
    private static final String CONTROLLER_PACKAGE_NAME = "com.dev.servlet.adapter.in.web.controller.internal.";
    private static final ConcurrentMap<String, Class<?>> classes = new ConcurrentHashMap<>();
    @Getter
    public static final DependencyResolver resolver = new DependencyResolver();

    public static <T> T resolve(Class<T> clazz) {
        return resolver.resolve(clazz);
    }

    public static class DependencyResolver {
        public <T> T resolve(Class<T> beanType) {
            return resolve(beanType, new Annotation[0]);
        }

        public <T> T resolve(Class<T> beanType, Annotation... qualifiers) {
            if (beanType == null) throw new IllegalArgumentException("beanType must not be null");
            try {
                return CDI.current().select(beanType,qualifiers).get();
            } catch (Exception e) {
                log.error("Failed to resolve bean: {}", beanType.getName(), e);
                return null;
            }
        }

        public Object getBean(String beanName) {
            Class<?> clazz = classes.computeIfAbsent(beanName, (data) -> {
                try {
                    return ClassUtils.getClass(CONTROLLER_PACKAGE_NAME + beanName);
                } catch (Exception e) {
                    log.error("Error resolving service: {}", beanName, e);
                    return null;
                }
            });

            Objects.requireNonNull(clazz, "Bean type not found for name: " + beanName);
            return resolve(clazz);
        }
    }
}
