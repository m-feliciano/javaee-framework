package com.dev.servlet.infrastructure.config;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;

@RequestScoped
public class EntityManagerProducer {

    @Inject
    private EntityManagerFactoryProvider provider;

    @Produces
    public Session produce() {
        return (Session) provider.create();
    }

    public void close(@Disposes EntityManager em) {
        if (em.isOpen()) em.close();
    }
}