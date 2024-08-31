package com.dev.servlet.listeners;

import com.dev.servlet.providers.ServiceLocator;
import com.dev.servlet.utils.JPAUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * The listener interface for receiving context events.
 *
 * @since 1.3.0
 */
@WebListener
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
        ServiceLocator.getInstance().resolveAll();
    }
}