package com.servletstack.infrastructure.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import static com.servletstack.infrastructure.config.Properties.loadDatabaseProperties;

@ApplicationScoped
public class EntityManagerFactoryProvider {

    private EntityManagerFactory emf;

    @PostConstruct
    void init() {
        this.emf = Persistence.createEntityManagerFactory("servletpu", loadDatabaseProperties());
    }

    public EntityManager create() {
        return emf.createEntityManager();
    }

    @PreDestroy
    void shutdown() {
        if (emf != null) emf.close();
    }
}
