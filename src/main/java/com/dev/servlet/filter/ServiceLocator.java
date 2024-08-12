package com.dev.servlet.filter;

import com.dev.servlet.interfaces.Inject;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServiceLocator {
    private final EntityManager entityManager;
    private final Map<Class<?>, Object> services;
    private final Map<Class<?>, Integer> creationInProgress;
    public static final int MAX_DEPTH = 2;
    private static final Logger LOGGER = Logger.getLogger(ServiceLocator.class.getName());

    public ServiceLocator(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.services = new HashMap<>();
        this.creationInProgress = new HashMap<>();
    }

    private void injectServices(Object newInstance, int depth) {
        for (Field f : newInstance.getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(Inject.class)) {
                try {
                    f.setAccessible(true);

                    Class<?> serviceClass = f.getType();
                    int currentDepth = creationInProgress.getOrDefault(serviceClass, 0);
                    if (currentDepth > MAX_DEPTH) {
                        continue;
                    }

                    Object injectable = getService(serviceClass, depth + 1);

                    f.set(newInstance, injectable);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to inject service: %s".formatted(f.getType()), e);
                } finally {
                    f.setAccessible(false);
                }
            }
        }
    }

    public <T> T getService(Class<T> serviceClass) {
        return getService(serviceClass, 0);
    }

    private <T> T getService(Class<T> serviceClass, int depth) {
        if (services.containsKey(serviceClass)) {
            return serviceClass.cast(services.get(serviceClass));
        } else {
            try {
                creationInProgress.put(serviceClass, depth);

                T service = serviceClass.getDeclaredConstructor(EntityManager.class)
                        .newInstance(entityManager);
                injectServices(service, depth);
                services.put(serviceClass, service);
                return service;
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to instantiate service: %s".formatted(serviceClass), e);
            } finally {
                creationInProgress.remove(serviceClass);
            }
        }
        return null;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}