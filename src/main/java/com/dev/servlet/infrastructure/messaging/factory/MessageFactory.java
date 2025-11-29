package com.dev.servlet.infrastructure.messaging.factory;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.activemq.artemis.api.core.client.ServerLocator;

@Slf4j
@ApplicationScoped
public class MessageFactory {
    public static ClientSessionFactory createSessionFactory(String brokerUrl) {
        try {
            ServerLocator locator = ActiveMQClient.createServerLocator(brokerUrl);
            return locator.createSessionFactory();
        } catch (Exception e) {
            log.error("Error initializing JMS ConnectionFactory: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
