package com.dev.servlet.providers;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is responsible is an entry point for the service locator
 */
@ApplicationScoped
public class ServiceLocator {

    /**
     * This class represents the service locator instance
     */
    @Singleton
    public static class ServiceLocatorInstance {
        private final Map<Class<?>, Object> services;
        private final Logger logger;

        private ServiceLocatorInstance() {
            services = new ConcurrentHashMap<>();
            logger = Logger.getLogger(ServiceLocatorInstance.class.getName());
        }

        /**
         * This method is responsible for getting an instance of the service
         *
         * @param serviceClass
         * @param <T>
         * @return
         */
        public <T> T getService(Class<T> serviceClass) {
            if (services.containsKey(serviceClass)) {
                return serviceClass.cast(services.get(serviceClass));
            } else {
                try {
                    // Look up the CDI bean manager and use it to resolve the bean
                    BeanManager beanManager = CDI.current().getBeanManager();
                    Bean<?> bean = beanManager.resolve(beanManager.getBeans(serviceClass));
                    T service = serviceClass.cast(
                            beanManager.getReference(
                                    bean, serviceClass, beanManager.createCreationalContext(bean)));
                    services.put(serviceClass, service);
                    return service;
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Failed to instantiate service: %s".formatted(serviceClass), e);
                }
            }
            return null;
        }

    }

    // Singleton instance
    private static ServiceLocatorInstance instance;

    public ServiceLocator() {
        // Empty constructor
    }

    /**
     * This method is responsible for getting the service locator instance
     *
     * @return
     */
    public static ServiceLocatorInstance getInstance() {
        if (instance != null) return instance;

        synchronized (ServiceLocator.class) {
            if (instance == null) {
                instance = new ServiceLocatorInstance();
            }
        }
        return instance;
    }
}