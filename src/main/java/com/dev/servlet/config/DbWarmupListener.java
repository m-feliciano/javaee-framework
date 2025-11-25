package com.dev.servlet.config;

import com.dev.servlet.core.util.CacheUtils;
import com.dev.servlet.domain.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;

@Slf4j
@WebListener
public class DbWarmupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("Starting database warmup...");
        try {
            EntityManagerFactory emf = EntityManagerProducer.createEntityManagerFactory("servletpu");
            try (EntityManager em = emf.createEntityManager()) {

                em.createNativeQuery("SELECT 1").getSingleResult();
                try (Session session = em.unwrap(Session.class)) {
                    session.createQuery("FROM User u WHERE u.status = 'A'", User.class)
                            .setCacheable(true)
                            .setMaxResults(1)
                            .list();
                }
            }

            log.info("Database pool warmed up successfully");
        } catch (Exception e) {
            log.error("Pool warmup failed: {}", e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("Graceful shutdown initiated...");
        try {
            CacheUtils.close();
            log.info("Cache closed successfully");
        } catch (Exception e) {
            log.error("Error during shutdown: {}", e.getMessage(), e);
        }
        log.info("Graceful shutdown completed");
    }
}
