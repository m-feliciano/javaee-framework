package com.dev.servlet.adapter.out.messaging.producer;

import com.dev.servlet.adapter.out.messaging.Message;
import com.dev.servlet.adapter.out.messaging.factory.MessageFactory;
import com.dev.servlet.application.port.out.MessagePort;
import com.dev.servlet.domain.enums.MessageType;
import com.dev.servlet.infrastructure.config.Properties;
import com.dev.servlet.shared.util.CloneUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.DeliveryMode;
import jakarta.jms.Destination;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;

import static com.dev.servlet.infrastructure.config.Properties.getEnvOrDefault;

@Slf4j
@ApplicationScoped
@Named("jmsMessageProducer")
public class JmsMessageProducer implements MessagePort {
    private static final String EMAIL_EXCHANGE_QUEUE = "email.exchange.queue";

    private Connection connection;
    private Session session;
    private jakarta.jms.MessageProducer producer;

    @PostConstruct
    public void init() {
        if ("jms".equals(Properties.get("provider.broker"))) {
            String brokerUrl = getEnvOrDefault("BROKER_URL", "tcp://activemq:61616");
            String user = getEnvOrDefault("BROKER_USER", "artemis");
            String pass = getEnvOrDefault("BROKER_PASSWORD", "artemis");
            startProducer(brokerUrl, user, pass);

        } else {
            log.info("MessageBrokerConfig: JMS provider not configured, skipping JmsMessageProducer initialization");
        }
    }

    @Override
    public void send(Message message) {
        try {
            log.info("Preparing to send message to queue {} for email={}", EMAIL_EXCHANGE_QUEUE, message.toEmail());

            String json = CloneUtil.toJson(message);
            TextMessage textMessage = session.createTextMessage(json);
            producer.send(textMessage);

            log.info("Message sent to queue {} for email={}", EMAIL_EXCHANGE_QUEUE, message.toEmail());

        } catch (Exception e) {
            log.error("Failed to send message to queue {}", EMAIL_EXCHANGE_QUEUE, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendConfirmation(String email, String link) {
        String createdAt = OffsetDateTime.now().toString();
        send(new Message(MessageType.CONFIRMATION, email, createdAt, link));
    }

    @Override
    public void sendWelcome(String email) {
        String createdAt = OffsetDateTime.now().toString();
        send(new Message(MessageType.WELCOME, email, createdAt, null));
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down JMS MessageProducer");
        safeClose(producer);
        safeClose(session);
        safeClose(connection);
    }

    private void startProducer(String brokerUrl, String user, String pass) {
        log.info("MessageBrokerConfig: emailConfirmationQueue configured with brokerUrl={}, user={}", brokerUrl, user);

        try {
            ConnectionFactory factory = MessageFactory.createConnectionFactory(brokerUrl, user, pass);

            this.connection = factory.createConnection();
            this.connection.start();

            this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination queue = session.createQueue(EMAIL_EXCHANGE_QUEUE);
            this.producer = session.createProducer(queue);

            this.producer.setDeliveryMode(DeliveryMode.PERSISTENT);

            log.info("JMS MessageProducer started for queue {}", EMAIL_EXCHANGE_QUEUE);

        } catch (Exception e) {
            log.error("Failed to start JMS MessageProducer", e);
            throw new IllegalStateException("JMS producer startup failed", e);
        }
    }

    private void safeClose(AutoCloseable c) {
        try {
            c.close();
        } catch (Exception e) {
            log.warn("Failed to close JMS resource: {}", e.getMessage());
        }
    }
}
