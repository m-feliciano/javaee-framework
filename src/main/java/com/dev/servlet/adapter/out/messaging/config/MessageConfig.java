package com.dev.servlet.adapter.out.messaging.config;

import lombok.extern.slf4j.Slf4j;

import static com.dev.servlet.infrastructure.config.Properties.getEnvOrDefault;

@Slf4j
public class MessageConfig {
    public static String EMAIL_EXCHANGE_QUEUE = "email.exchange.queue";

    public static BrokerConfig createBrokerConfig(String name) {
        String brokerUrl = getEnvOrDefault("BROKER_URL", "tcp://activemq:61616");
        String user = getEnvOrDefault("BROKER_USER", "artemis");
        String pass = getEnvOrDefault("BROKER_PASSWORD", "artemis");
        log.info("MessageBrokerConfig: emailConfirmationQueue configured with brokerUrl={}, user={}", brokerUrl, user);
        return new BrokerConfig(brokerUrl, user, pass, name);
    }

    public record BrokerConfig(String brokerUrl, String user, String pass, String queueName) {
    }
}
