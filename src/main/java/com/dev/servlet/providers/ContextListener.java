package com.dev.servlet.providers;

import com.dev.servlet.utils.ClassUtil;
import com.dev.servlet.utils.CollectionUtils;
import com.dev.servlet.utils.JPAUtil;

import javax.inject.Singleton;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;

/**
 * The listener interface for receiving context events.
 *
 * @since 1.3.0
 */
public class ContextListener implements ServletContextListener {

    /**
     * This method is called when the servlet context is destroyed
     *
     * @param arg0
     */
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // Add the code to destroy the servlet context
        JPAUtil.closeEntityManagerFactory();
    }

    /**
     * This method is called when the servlet context is initialized
     *
     * @param arg0
     */
    @Override
    @SuppressWarnings("unchecked")
    public void contextInitialized(ServletContextEvent arg0) {
        // Add the code to initialize the servlet context
        List<Class<?>> clazzList = null;
        try {
            clazzList = ClassUtil.loadClasses("com.dev.servlet.business", new Class[]{Singleton.class});
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!CollectionUtils.isNullOrEmpty(clazzList)) {
            for (Class<?> bean : clazzList) {
                ServiceLocator.getInstance().getService(bean);
            }
        }
    }
}