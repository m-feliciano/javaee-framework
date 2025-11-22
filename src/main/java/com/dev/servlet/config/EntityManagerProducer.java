package com.dev.servlet.config;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
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
    private static final String ORG_HIBERNATE_DIALECT = "org.hibernate.dialect.PostgreSQL82Dialect";

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
        if (em.isOpen()) {
            em.close();
        }
    }

    private static void loadProps(Map<String, Object> props) {
        String dbHost = System.getenv(DB_HOST);
        String dbPort = System.getenv(DB_PORT);
        String dbName = System.getenv(POSTGRES_DB);
        String dbUser = System.getenv(POSTGRES_USER);
        String dbPassword = System.getenv(POSTGRES_PASSWORD);

        String databaseUrl = String.format("jdbc:postgresql://%s:%s/%s?useSSL=false", dbHost, dbPort, dbName);

        props.put("jakarta.persistence.jdbc.url", databaseUrl);
        props.put("jakarta.persistence.jdbc.user", dbUser);
        props.put("jakarta.persistence.jdbc.password", dbPassword);
        props.put("jakarta.persistence.jdbc.driver", ORG_POSTGRESQL_DRIVER);
        props.put("hibernate.dialect", ORG_HIBERNATE_DIALECT);
    }
}
