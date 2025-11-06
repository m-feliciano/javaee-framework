package com.dev.servlet.config;

import com.dev.servlet.core.util.CacheUtils;
import com.dev.servlet.domain.model.User;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@Slf4j
@WebListener
public class DbWarmupListener implements ServletContextListener {

    @Inject
    private EntityManagerProducer producer;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("Starting database warmup...");
        new Thread(() -> {
            try {
                SessionFactory factory = producer.getEntityManager().getSessionFactory();

                try (Session session = factory.openSession()) {
                    session.createNativeQuery("SELECT 1").getSingleResult();
                }

                try (Session session = factory.openSession()) {
                    session.createQuery("FROM User WHERE status = 'A'", User.class)
                            .setCacheable(true)
                            .setMaxResults(1)
                            .list();
                }

                log.info("Database pool warmed up successfully");
            } catch (Exception e) {
                log.error("Pool warmup failed: {}", e.getMessage(), e);
            }
        }).start();
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
