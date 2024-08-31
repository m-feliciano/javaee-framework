package com.dev.servlet.providers;

import com.dev.servlet.interfaces.IService;
import com.dev.servlet.utils.ClassUtil;
import com.dev.servlet.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is responsible is an entry point for the service locator
 */
public final class ServiceLocator {

    /**
     * This class represents the service locator instance
     */
    @Singleton
    public static class ServiceLocatorInstance {
        private final Map<String, Object> services;
        private final Logger logger;

        private ServiceLocatorInstance() {
            services = new ConcurrentHashMap<>();
            logger = LoggerFactory.getLogger(ServiceLocatorInstance.class.getName());
        }

        /**
         * This method is responsible for resolving all the services
         *
         * @since 1.3.4
         */
        @SuppressWarnings({"unchecked"})
        public synchronized void resolveAll() {
            List<Class<?>> clazzList = null;
            Class<IService> annotation = IService.class;

            try {
                clazzList = ClassUtil.loadClasses("com.dev.servlet.business", new Class[]{annotation});
            } catch (Exception e) {
                logger.error("Failed to load classes", e);
            }

            if (!CollectionUtils.isNullOrEmpty(clazzList)) {
                for (Class<?> aClass : clazzList) {
                    try {
                        BeanManager beanManager = CDI.current().getBeanManager();
                        Bean<?> bean = beanManager.resolve(beanManager.getBeans(aClass));
                        Object service = beanManager.getReference(
                                bean, bean.getBeanClass(), beanManager.createCreationalContext(bean));
                        services.putIfAbsent(aClass.getAnnotation(annotation).value(), service);

                    } catch (Exception e) {
                        logger.error("Failed to instantiate service: %s".formatted(aClass.getName()), e);
                    }
                }
            }
        }

        /**
         * This method is responsible for getting an instance of the service
         *
         * @param serviceName
         * @return
         */
        public Object getService(String serviceName) {
            return services.get(serviceName);
        }
    }

    // Singleton instance
    private static ServiceLocatorInstance instance;

    private ServiceLocator() {
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