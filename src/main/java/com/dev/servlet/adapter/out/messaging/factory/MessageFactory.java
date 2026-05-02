package com.dev.servlet.adapter.out.messaging.factory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.jms.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

@Slf4j
@ApplicationScoped
public class MessageFactory {

    public static ConnectionFactory createConnectionFactory(String brokerUrl, String user, String pass) {
        return new ActiveMQConnectionFactory(brokerUrl, user, pass);
    }
}
