package com.dev.servlet.adapter.out.messaging.producer;

import com.dev.servlet.adapter.out.messaging.Message;
import com.dev.servlet.adapter.out.messaging.config.MessageConfig;
import com.dev.servlet.adapter.out.messaging.factory.MessageFactory;
import com.dev.servlet.application.port.out.MessagePort;
import com.dev.servlet.domain.enums.MessageType;
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

import static com.dev.servlet.adapter.out.messaging.config.MessageConfig.EMAIL_EXCHANGE_QUEUE;

@Slf4j
@ApplicationScoped
@Named("messageProducer")
public class MessageProducer implements MessagePort {

    private Connection connection;
    private Session session;
    private jakarta.jms.MessageProducer producer;

    @PostConstruct
    public void init() {
        MessageConfig.BrokerConfig config = MessageConfig.createBrokerConfig(EMAIL_EXCHANGE_QUEUE);

        try {
            ConnectionFactory factory = MessageFactory.createConnectionFactory(
                    config.brokerUrl(),
                    config.user(),
                    config.pass()
            );

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
        send(new Message(
                MessageType.CONFIRMATION,
                email,
                OffsetDateTime.now().toString(),
                link
        ));
    }

    @Override
    public void sendWelcome(String email) {
        send(new Message(
                MessageType.WELCOME,
                email,
                OffsetDateTime.now().toString(),
                null
        ));
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down JMS MessageProducer");
        safeClose(producer);
        safeClose(session);
        safeClose(connection);
    }

    private void safeClose(AutoCloseable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Exception e) {
                log.warn("Failed to close JMS resource: {}", e.getMessage());
            }
        }
    }
}
