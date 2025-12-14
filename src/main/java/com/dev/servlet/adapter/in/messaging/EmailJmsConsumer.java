package com.dev.servlet.adapter.in.messaging;

import com.dev.servlet.adapter.out.messaging.Message;
import com.dev.servlet.adapter.out.messaging.config.MessageConfig;
import com.dev.servlet.adapter.out.messaging.factory.MessageFactory;
import com.dev.servlet.adapter.out.messaging.registry.MessageServiceRegistry;
import com.dev.servlet.domain.enums.MessageType;
import com.dev.servlet.shared.util.CloneUtil;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageListener;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import static com.dev.servlet.adapter.out.messaging.config.MessageConfig.EMAIL_EXCHANGE_QUEUE;

@Slf4j
@ApplicationScoped
public class EmailJmsConsumer implements MessageListener {

    private Connection connection;
    private Session session;
    private MessageConsumer consumer;

    @Inject
    private MessageServiceRegistry messageServiceRegistry;

    public void onStartup(@Observes @Initialized(ApplicationScoped.class) Object init) {
        MessageConfig.BrokerConfig config = MessageConfig.createBrokerConfig(EMAIL_EXCHANGE_QUEUE);
        this.initConsumerWithRetry(config);
    }

    private void initConsumerWithRetry(MessageConfig.BrokerConfig config) {
        ConnectionFactory factory = MessageFactory.createConnectionFactory(
                config.brokerUrl(),
                config.user(),
                config.pass()
        );

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
            var handler = messageServiceRegistry.getConsumer(type);

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
        try {
            if (consumer != null) consumer.close();
        } catch (Exception ignored) {
        }
        try {
            if (session != null) session.close();
        } catch (Exception ignored) {
        }
        try {
            if (connection != null) connection.close();
        } catch (Exception ignored) {
        }
    }
}
