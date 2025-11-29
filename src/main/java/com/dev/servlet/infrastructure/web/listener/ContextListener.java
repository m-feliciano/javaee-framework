package com.dev.servlet.infrastructure.web.listener;

import com.dev.servlet.infrastructure.cache.CacheUtils;
import com.dev.servlet.infrastructure.config.Properties;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

@Slf4j
@WebListener
public class ContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        ServletContext context = arg0.getServletContext();
        context.setAttribute("systemVersion", Properties.getOrDefault("system.version", "unknown"));
        context.setAttribute("environment", Properties.getOrDefault("app.env", "unknown"));
        // Disable session tracking via URL rewriting
        context.setSessionTrackingModes(Collections.emptySet());
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        try {
            CacheUtils.close();
        } catch (Exception ignored) {
        }
    }
}
