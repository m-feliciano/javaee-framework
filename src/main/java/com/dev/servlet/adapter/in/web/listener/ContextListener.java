package com.dev.servlet.adapter.in.web.listener;

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

        String systemVersion = Properties.getOrDefault("system.version", "unknown");
        String appEnv = Properties.getOrDefault("app.env", "unknown");

        context.setAttribute("systemVersion", systemVersion);
        context.setAttribute("environment", appEnv);

        String domain = Properties.getOrDefault("cdn.domain", "unknown");
        String protocol = Properties.getOrDefault("cdn.protocol", "unknown");
        context.setAttribute("cdn", protocol + "://" + domain);

        boolean demoMode = Properties.isDemoModeEnabled();
        if (demoMode)
            log.info("ContextListener: DEMO_MODE is enabled");
        context.setAttribute("demoMode", demoMode);

        // Disable session tracking via URL rewriting
        context.setSessionTrackingModes(Collections.emptySet());
    }
}
