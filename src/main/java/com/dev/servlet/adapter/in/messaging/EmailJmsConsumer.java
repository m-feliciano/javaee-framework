package com.dev.servlet.adapter.in.messaging;

import com.dev.servlet.adapter.out.messaging.Message;
import com.dev.servlet.adapter.out.messaging.registry.MessageServiceRegistry;
import com.dev.servlet.domain.enums.MessageType;
import com.dev.servlet.infrastructure.config.Properties;
import com.dev.servlet.shared.util.CloneUtil;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageListener;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

import static com.dev.servlet.adapter.out.messaging.factory.MessageFactory.createConnectionFactory;
import static com.dev.servlet.infrastructure.config.Properties.getEnv;
import static com.dev.servlet.infrastructure.config.Properties.getEnvOrDefault;

@Slf4j
@ApplicationScoped
@Named("emailJmsConsumer")
public class EmailJmsConsumer implements MessageListener {
    private static final String EMAIL_EXCHANGE_QUEUE = "email.exchange.queue";

    private Connection connection;
    private Session session;
    private MessageConsumer consumer;

    @Inject
    private MessageServiceRegistry registry;

    public void onStartup(@Observes @Initialized(ApplicationScoped.class) Object init) {
        if ("jms".equals(Properties.get("provider.broker"))) {
            String brokerUrl = getEnvOrDefault("BROKER_URL", "tcp://activemq:61616");
            String user = getEnv("BROKER_USER");
            String pass = getEnv("BROKER_PASSWORD");
            log.info("EmailJmsConsumer: emailConfirmationQueue configured with brokerUrl={}, user={}", brokerUrl, user);

            this.initConsumerWithRetry(createConnectionFactory(brokerUrl, user, pass));

        } else {
            log.info("EmailJmsConsumer: JMS provider not configured, skipping EmailJmsConsumer initialization");
        }
    }

    private void initConsumerWithRetry(ConnectionFactory factory) {

        int attempt = 0;
        long delay = 2000;
        long maxDelay = 30000;

        while (!Thread.currentThread().isInterrupted()) {
            try {
                attempt++;

                this.connection = factory.createConnection();
                this.connection.start();

                this.session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
                Destination queue = session.createQueue(EMAIL_EXCHANGE_QUEUE);

                this.consumer = session.createConsumer(queue);
                this.consumer.setMessageListener(this);

                log.info("EmailJmsConsumer started after {} attempt(s)", attempt);
                return;

            } catch (Exception e) {
                log.warn("Failed to start EmailJmsConsumer (attempt {}), retrying in {} ms", attempt, delay);

                safeClose();
                sleep(delay);

                delay = Math.min(delay * 2, maxDelay);
            }
        }
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void onMessage(jakarta.jms.Message jmsMessage) {
        try {
            if (!(jmsMessage instanceof TextMessage textMessage)) {
                log.warn("Unsupported JMS message type {}", jmsMessage.getClass());
                jmsMessage.acknowledge();
                return;
            }

            Message message;
            try {
                String payload = textMessage.getText();
                if (StringUtils.isBlank(payload)) {
                    log.warn("Received blank JMS message, acknowledging");
                    throw new IllegalArgumentException("Blank message payload");
                }

                message = CloneUtil.fromJson(payload, Message.class);
                if (message == null || message.type() == null) {
                    log.warn("Invalid message payload, acknowledging");
                    throw new IllegalArgumentException("Invalid message payload");
                }

            } catch (Exception e) {
                log.warn("Failed to deserialize message payload, acknowledging", e);
                jmsMessage.acknowledge();
                return;
            }

            MessageType type = message.type();
            var handler = registry.getConsumer(type);

            if (handler == null) {
                log.warn("No handler registered for message type {}, acknowledging", type);
                jmsMessage.acknowledge();
                return;
            }

            handler.accept(message);

            jmsMessage.acknowledge();

            log.info("Message {} processed and acknowledged", message.id());

        } catch (Exception e) {
            log.error("Error processing JMS message, will be redelivered", e);
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down EmailJmsConsumer");
        safeClose();
    }

    private void safeClose() {
        Arrays.asList(consumer, session, connection)
                .forEach(c -> {
                    try {
                        c.close();
                    } catch (Exception ignored) {
                    }
                });
    }
}
