package com.dev.servlet.providers;

import com.dev.servlet.interfaces.IService;
import com.dev.servlet.utils.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.spi.CreationalContext;
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

@SuppressWarnings("unchecked")
public final class ServiceLocator {

    /**
     * This class represents the service locator instance
     */
    @Singleton
    public static class ServiceLocatorInstance {
        private final Map<String, Class<?>> services;
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
        public synchronized void resolveAll() {
            List<Class<?>> clazzList;
            try {
                clazzList = ClassUtil.loadClasses("com.dev.servlet.business", new Class[]{IService.class});
            } catch (Exception e) {
                logger.error("Failed to load classes", e);
                return;
            }

            for (Class<?> aClass : clazzList) {
                IService ann = aClass.getAnnotation(IService.class);
                services.putIfAbsent(ann.value(), aClass);
            }
        }

        /**
         * This method is responsible for resolving the service
         *
         * @param aClass
         * @return
         */
        private Object resolve(Class<?> aClass) {
            try {
                BeanManager beanManager = CDI.current().getBeanManager();
                Bean<?> bean = beanManager.resolve(beanManager.getBeans(aClass));
                CreationalContext<?> ctx = beanManager.createCreationalContext(bean);
                return beanManager.getReference(
                        bean, bean.getBeanClass(), ctx);
            } catch (Exception e) {
                logger.error("Failed to instantiate service: {}", aClass.getName(), e);
                return null;
            }
        }

        /**
         * This method is responsible for getting an instance of the service
         *
         * @param serviceName
         * @return
         */
        public Object getService(String serviceName) {
            Class<?> aClass = services.get(serviceName);
            return resolve(aClass);
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