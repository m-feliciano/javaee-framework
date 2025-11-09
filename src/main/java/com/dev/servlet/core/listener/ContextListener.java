package com.dev.servlet.core.listener;

import com.dev.servlet.core.util.CacheUtils;
import com.dev.servlet.core.util.PropertiesUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Collections;

@WebListener
public class ContextListener implements ServletContextListener {
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        CacheUtils.close();
    }
    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        ServletContext context = arg0.getServletContext();
        context.setAttribute("systemVersion", PropertiesUtil.getProperty("system.version"));
        context.setSessionTrackingModes(Collections.emptySet());
    }
}
