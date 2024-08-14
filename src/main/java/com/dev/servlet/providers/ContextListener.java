package com.dev.servlet.providers;

import com.dev.servlet.utils.ClassUtil;
import com.dev.servlet.utils.CollectionUtils;
import com.dev.servlet.utils.JPAUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.ArrayList;
import java.util.List;

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
    public void contextInitialized(ServletContextEvent arg0) {
        // Add the code to initialize the servlet context
        List<Class<?>> clazzList = new ArrayList<>();
        try {
            clazzList = ClassUtil.loadClasses("com.dev.servlet.view", true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!CollectionUtils.isNullOrEmpty(clazzList)) {
            clazzList.forEach(bean -> ServiceLocator.getInstance().getService(bean));
        }
    }
}