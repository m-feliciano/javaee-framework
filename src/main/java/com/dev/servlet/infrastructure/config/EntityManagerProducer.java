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
        return (Session) em;
    }

    public void close(@Disposes EntityManager em) {
        if (em.isOpen()) em.close();
    }
}