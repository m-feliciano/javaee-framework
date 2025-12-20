package com.dev.servlet.infrastructure.config;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;

@Slf4j
@RequestScoped
public class EntityManagerProducer {

    @Inject
    private EntityManagerFactoryProvider provider;

    @Produces
    public Session produce() {
        log.info("EM OPEN {}", System.identityHashCode(this));
        return (Session) provider.create();
    }

    public void close(@Disposes EntityManager em) {
        log.info("EM CLOSE {}", System.identityHashCode(em));
        if (em.isOpen()) em.close();
    }
}