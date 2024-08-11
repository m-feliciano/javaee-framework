package com.dev.servlet.filter;

import com.dev.servlet.interfaces.Inject;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ServiceLocator {
    private final EntityManager entityManager;
    private final Map<Class<?>, Object> services;

    public ServiceLocator(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.services = new HashMap<>();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public <T> T getService(Class<T> serviceClass) {
        if (services.containsKey(serviceClass)) {
            T service = serviceClass.cast(services.get(serviceClass));
            return service;
        } else {
            try {
                T service = serviceClass.getDeclaredConstructor(EntityManager.class)
                        .newInstance(entityManager);
                injectServices(service);
                services.put(serviceClass, service);
                return service;
            } catch (Exception e) {
                throw new RuntimeException("Failed to instantiate service: " + serviceClass, e);
            }
        }
    }

    /**
     * Inject the services
     *
     * @param newInstance
     */
    private void injectServices(Object newInstance) {
        Arrays.stream(newInstance.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Inject.class))
                .forEach(f -> {
                    try {
                        f.setAccessible(true);

                        Object injectable = f.getType()
                                .getDeclaredConstructor(EntityManager.class)
                                .newInstance(this.entityManager);

                        f.set(newInstance, injectable);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to inject service: " + f.getType(), e);
                    } finally {
                        f.setAccessible(false);
                    }
                });
    }
}