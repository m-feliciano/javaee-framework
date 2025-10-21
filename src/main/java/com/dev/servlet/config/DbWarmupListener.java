package com.dev.servlet.config;

import com.dev.servlet.domain.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class DbWarmupListener implements ServletContextListener {

    @Inject
    private EntityManagerProducer producer;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        new Thread(() -> {
            try {
                SessionFactory factory = producer.getEntityManager().getSessionFactory();
                // Warm up connection pool
                try (Session session = factory.openSession()) {
                    session.createNativeQuery("SELECT 1").getSingleResult();
                }

                // Warm up query cache
                try (Session session = factory.openSession()) {
                    session.createQuery("FROM User WHERE status = 'A'", User.class)
                            .setCacheable(true)
                            .setMaxResults(1)
                            .list();
                }

                System.out.println("✅ Database pool warmed up");
            } catch (Exception e) {
                System.err.println("⚠️ Pool warmup failed: " + e.getMessage());
            }
        }).start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // No action needed on context destruction
    }
}
