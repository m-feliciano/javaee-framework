// java
package com.dev.servlet.config;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Setter
@ApplicationScoped
public class EntityManagerProducer {

    private static final String DB_HOST = "DB_HOST";
    private static final String DB_PORT = "DB_PORT";
    private static final String POSTGRES_DB = "POSTGRES_DB";
    private static final String POSTGRES_USER = "POSTGRES_USER";
    private static final String POSTGRES_PASSWORD = "POSTGRES_PASSWORD";
    private static final String ORG_POSTGRESQL_DRIVER = "org.postgresql.Driver";

    private EntityManagerFactory factory;

    public EntityManagerProducer() {
        Map<String, Object> props = new HashMap<>();
        loadProps(props);

        try {
            factory = Persistence.createEntityManagerFactory("servletpu", props);
        } catch (Exception e) {
            log.error("[SEVERE] Failed to create EntityManagerFactory: {}", e.getMessage(), e);
            throw e;
        }
    }

    public static EntityManagerFactory createEntityManagerFactory(String servletpu) {
        Map<String, Object> props = new HashMap<>();
        loadProps(props);
        return Persistence.createEntityManagerFactory(servletpu, props);
    }

    @Produces
    @RequestScoped
    public Session getEntityManager() {
        if (factory == null) {
            log.error("EntityManagerFactory is null. Check persistence.xml configuration.");
            throw new IllegalStateException("EntityManagerFactory is not initialized.");
        }
        return (Session) factory.createEntityManager();
    }

    @PreDestroy
    public void closeEntityManagerFactory() {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
    }

    public void close(@Disposes EntityManager em) {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    private static void loadProps(Map<String, Object> props) {
        String dbHost = System.getenv(DB_HOST);
        String dbPort = System.getenv(DB_PORT);
        String dbName = System.getenv(POSTGRES_DB);
        String dbUser = System.getenv(POSTGRES_USER);
        String dbPassword = System.getenv(POSTGRES_PASSWORD);

        String databaseUrl = "jdbc:postgresql://%s:%s/%s?useSSL=false".formatted(dbHost, dbPort, dbName);

        props.put("jakarta.persistence.jdbc.url", databaseUrl);
        props.put("jakarta.persistence.jdbc.user", dbUser);
        props.put("jakarta.persistence.jdbc.password", dbPassword);
        props.put("jakarta.persistence.jdbc.driver", ORG_POSTGRESQL_DRIVER);
    }
}
