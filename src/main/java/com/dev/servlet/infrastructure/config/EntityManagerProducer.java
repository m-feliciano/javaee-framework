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
        EntityManager em = provider.create();
        log.debug("EM OPEN {}", System.identityHashCode(em));
        return (Session) em;
    }

    public void close(@Disposes EntityManager em) {
        log.debug("EM CLOSE {}", System.identityHashCode(em));
        if (em.isOpen()) em.close();
    }
}