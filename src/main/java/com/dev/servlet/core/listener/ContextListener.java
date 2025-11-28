package com.dev.servlet.core.listener;

import com.dev.servlet.config.EntityManagerProducer;
import com.dev.servlet.core.util.CacheUtils;
import com.dev.servlet.core.util.Properties;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;

import java.util.Collections;

import static com.dev.servlet.core.util.Properties.loadDatabaseProperties;

@Slf4j
@WebListener
public class ContextListener implements ServletContextListener {

    @Inject
    private EntityManagerProducer emp;

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        try {
            CacheUtils.close();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        ServletContext context = arg0.getServletContext();
        try {
            initializeMigration();
            emp.setupEmFactory();
            log.info("Database migration completed successfully");
            context.setAttribute("db.migration.status", "success");

        } catch (Exception e) {
            log.error("Database migration failed: {}", e.getMessage(), e);
            context.setAttribute("db.migration.status", "failed");
        }

        context.setAttribute("systemVersion", Properties.getOrDefault("system.version", "unknown"));
        context.setAttribute("environment", Properties.getOrDefault("app.env", "unknown"));
        // Disable session tracking via URL rewriting
        context.setSessionTrackingModes(Collections.emptySet());
    }

    private static void initializeMigration() {
        java.util.Properties properties = loadDatabaseProperties();
        MigrateResult migrateResult = Flyway.configure()
                .dataSource(
                        properties.getProperty("jakarta.persistence.jdbc.url"),
                        properties.getProperty("jakarta.persistence.jdbc.user"),
                        properties.getProperty("jakarta.persistence.jdbc.password")
                )
                .load()
                .migrate();

        long time = migrateResult.getTotalMigrationTime();
        log.info("Database migration completed in {} ms with {} migrations applied",
                time, migrateResult.migrations.size());

        migrateResult.migrations.forEach(m ->
                log.info("Applied migration: {} - {}", m.version, m.description));
    }
}
