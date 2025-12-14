package com.dev.servlet.infrastructure.migration;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;

import java.util.Properties;

import static com.dev.servlet.infrastructure.config.Properties.loadDatabaseProperties;

@Slf4j
@ApplicationScoped
public class FlywayMigrationService {

    public void onStart(@Observes @Initialized(ApplicationScoped.class) Object init) {
        try {
            Properties props = loadDatabaseProperties();
            MigrateResult result = Flyway.configure()
                    .dataSource(
                            props.getProperty("jakarta.persistence.jdbc.url"),
                            props.getProperty("jakarta.persistence.jdbc.user"),
                            props.getProperty("jakarta.persistence.jdbc.password")
                    )
                    .connectRetries(10)
                    .connectRetriesInterval(5)
                    .load()
                    .migrate();
            log.info("Database migration completed in {} ms with {} migrations applied",
                    result.getTotalMigrationTime(), result.migrations.size());
        } catch (Exception e) {
            log.error("Database migration failed: {}", e.getMessage(), e);
            throw e;
        }
    }
}
