package com.dev.servlet.adapter.in.web.listener;

import com.dev.servlet.application.port.out.cache.CachePort;
import com.dev.servlet.infrastructure.config.Properties;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

@Slf4j
@WebListener
public class ContextListener implements ServletContextListener {

    @Inject
    private CachePort cachePort;

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        ServletContext context = arg0.getServletContext();
        context.setAttribute("systemVersion", Properties.getOrDefault("system.version", "unknown"));
        context.setAttribute("environment", Properties.getOrDefault("app.env", "unknown"));

        boolean demoMode = Properties.isDemoModeEnabled();
        if (demoMode)
            log.info("ContextListener: DEMO_MODE is enabled");
        context.setAttribute("demoMode", demoMode);

        // Disable session tracking via URL rewriting
        context.setSessionTrackingModes(Collections.emptySet());
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        try {
            this.cachePort.close();
        } catch (Exception ignored) {
        }
    }
}
