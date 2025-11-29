package com.dev.servlet.infrastructure.config;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;

import java.time.Duration;

import static com.dev.servlet.infrastructure.config.Properties.loadDatabaseProperties;

@Slf4j
@Setter
@NoArgsConstructor
@ApplicationScoped
public class EntityManagerProducer {
    private EntityManagerFactory factory;

    @Produces
    @RequestScoped
    public Session getEntityManager() {
        Duration seconds = Duration.ofSeconds(30);
        this.factory = createOrGetFactory(seconds.toMillis());
        if (factory == null) {
            log.error("EntityManagerFactory is null. Check persistence.xml configuration.");
            throw new IllegalStateException("EntityManagerFactory is not initialized.");
        }
        return (Session) factory.createEntityManager();
    }

    public void setupEmFactory() {
        synchronized (this) {
            if (this.factory == null) {
                try {
                    this.factory = Persistence.createEntityManagerFactory("servletpu", loadDatabaseProperties());
                } catch (Exception e) {
                    log.error("Failed to initialize EntityManagerFactory: {}", e.getMessage(), e);
                }
            }
        }
    }

    private EntityManagerFactory createOrGetFactory(long timeoutMillis) {
        long startTime = System.currentTimeMillis();
        while (factory == null && (System.currentTimeMillis() - startTime) < timeoutMillis) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Thread was interrupted while waiting for EntityManagerFactory initialization.", e);
            }
        }
        return factory;
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
}
