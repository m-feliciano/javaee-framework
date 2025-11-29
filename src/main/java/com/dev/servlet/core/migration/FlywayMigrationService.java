package com.dev.servlet.core.migration;

import com.dev.servlet.config.EntityManagerProducer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;

import java.util.Properties;

import static com.dev.servlet.core.util.Properties.loadDatabaseProperties;

@Slf4j
@ApplicationScoped
public class FlywayMigrationService {

    @Inject
    private EntityManagerProducer emp;

    public void onStart(@Observes @Initialized(ApplicationScoped.class) Object init) {
        try {
            Properties props = loadDatabaseProperties();
            MigrateResult result = Flyway.configure()
                    .dataSource(
                            props.getProperty("jakarta.persistence.jdbc.url"),
                            props.getProperty("jakarta.persistence.jdbc.user"),
                            props.getProperty("jakarta.persistence.jdbc.password")
                    )
                    .load()
                    .migrate();

            log.info("Database migration completed in {} ms with {} migrations applied",
                    result.getTotalMigrationTime(), result.migrations.size());
            emp.setupEmFactory();

        } catch (Exception e) {
            log.error("Database migration failed: {}", e.getMessage(), e);
            throw e;
        }
    }
}
