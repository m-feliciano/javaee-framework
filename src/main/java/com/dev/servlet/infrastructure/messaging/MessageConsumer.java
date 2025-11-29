package com.dev.servlet.infrastructure.messaging;

import com.dev.servlet.domain.enums.MessageType;
import com.dev.servlet.infrastructure.messaging.config.MessageConfig;
import com.dev.servlet.infrastructure.messaging.factory.MessageFactory;
import com.dev.servlet.infrastructure.messaging.registry.MessageServiceRegistry;
import com.dev.servlet.infrastructure.utils.CloneUtil;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.artemis.api.core.ActiveMQBuffer;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.QueueConfiguration;
import org.apache.activemq.artemis.api.core.client.ClientConsumer;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Consumer;

import static com.dev.servlet.infrastructure.messaging.config.MessageConfig.EMAIL_EXCHANGE_QUEUE;

@Slf4j
@ApplicationScoped
@Named("messageConsumer")
public class MessageConsumer {
    private MessageConfig.BrokerConfig config;
    private ClientSessionFactory factory;
    private volatile boolean running = true;
    private Thread consumerThread;
    @Inject
    private MessageServiceRegistry messageServiceRegistry;

    public void onStartup(@Observes @Initialized(ApplicationScoped.class) Object init) {
        this.config = MessageConfig.createBrokerConfig(EMAIL_EXCHANGE_QUEUE);
        this.factory = MessageFactory.createSessionFactory(config.brokerUrl());
        startConsumerThread();
    }

    @PreDestroy
    public void shutdown() {
        running = false;
        if (consumerThread != null) {
            try {
                consumerThread.interrupt();
                consumerThread.join(Duration.ofSeconds(5));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (factory != null) {
            try {
                factory.close();
            } catch (Exception e) {
                log.warn("Failed to close ClientSessionFactory: {}", e.getMessage(), e);
            }
        }
    }

    private void startConsumerThread() {
        consumerThread = Thread.ofVirtual()
                .name("email-consumer")
                .start(() -> {
                    log.info("EmailJmsConsumer: consumer thread started");
                    long backoffMillis = 1000;
                    final long maxBackoff = Duration.ofSeconds(30).toMillis();

                    while (running && !Thread.currentThread().isInterrupted()) {
                        try (ClientSession session = createSession()) {
                            ensureQueue(session);
                            session.start();
                            log.info("EmailJmsConsumer: session started");
                            backoffMillis = 1000;

                            consumeMessages(session, (message) -> {
                                try {
                                    MessageType messageType = message.type();
                                    if (messageType == null) {
                                        log.warn("Received message with null type, skipping ID={}, to email={}",
                                                message.id(), message.toEmail());
                                        return;
                                    }
                                    var consumer = messageServiceRegistry.getConsumer(messageType);
                                    if (consumer == null) {
                                        log.warn("No consumer registered for message type {}, skipping", messageType);
                                        return;
                                    }
                                    consumer.accept(message);
                                } catch (Exception e) {
                                    log.error("Error in message consumer processing: {}", e.getMessage(), e);
                                    throw e;
                                }
                            });
                        } catch (InterruptedException ie) {
                            log.info("Consumer thread interrupted, exiting");
                            Thread.currentThread().interrupt();
                            break;
                        } catch (Exception e) {
                            log.error("Error in EmailJmsConsumer thread: {}", e.getMessage(), e);
                            try {
                                long sleep = Math.min(backoffMillis, maxBackoff);
                                log.info("EmailJmsConsumer: backing off {} ms before retry", sleep);
                                // Wait before retrying
                                Thread.sleep(sleep);
                                // Exponential backoff
                                backoffMillis = Math.min(backoffMillis * 2, maxBackoff);

                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                    }
                    log.info("EmailJmsConsumer: consumer thread stopped");
                });
    }

    private ClientSession createSession() throws Exception {
        ClientSession session = factory.createSession(
                config.user(), config.pass(), false, false, false, false, 1);
        log.info("EmailJmsConsumer: session created (user={})", config.user());
        return session;
    }

    private void ensureQueue(ClientSession session) {
        QueueConfiguration configuration = QueueConfiguration.of(EMAIL_EXCHANGE_QUEUE)
                .setAddress(EMAIL_EXCHANGE_QUEUE)
                .setDurable(true);
        try {
            session.createQueue(configuration);
            log.info("EmailJmsConsumer: created/ensured queue {}", EMAIL_EXCHANGE_QUEUE);
        } catch (Exception e) {
            log.warn("EmailJmsConsumer: createQueue() warning: {}", e.getMessage());
        }
    }

    private void consumeMessages(ClientSession session, Consumer<Message> then) throws Exception {
        try (ClientConsumer consumer = session.createConsumer(EMAIL_EXCHANGE_QUEUE)) {
            log.info("EmailJmsConsumer: consumer created for queue {}", EMAIL_EXCHANGE_QUEUE);

            while (running && !Thread.currentThread().isInterrupted()) {
                ClientMessage clientMsg = consumer.receive(1000);
                if (clientMsg == null) continue;

                try {
                    ActiveMQBuffer buf = clientMsg.getDataBuffer();
                    if (!buf.readable()) {
                        log.warn("EmailJmsConsumer: received empty clientMsg, skipping");
                        commit(session, clientMsg);
                        continue;
                    }

                    String body = buf.readString();
                    if (StringUtils.isBlank(body)) {
                        log.warn("EmailJmsConsumer: received blank clientMsg, skipping");
                        commit(session, clientMsg);
                        continue;
                    }

                    log.debug("EmailJmsConsumer: received raw clientMsg: {}", body);

                    Message message;
                    try {
                        message = CloneUtil.fromJson(body, Message.class);
                        Objects.requireNonNull(message, "Deserialized message is null");
                        log.info("EmailJmsConsumer: deserialized message ID={} type={} to={}",
                                message.id(), message.type(), message.toEmail());
                    } catch (RuntimeException e) {
                        // invalid JSON / unknown enum - acknowledge and skip to avoid poison message loops
                        log.warn("Invalid message payload, sending to DLQ or skipping: {} - body={}", e.getMessage(), body);
                        commit(session, clientMsg);
                        continue;
                    }

                    try {
                        then.accept(message);
                        commit(session, clientMsg);
                        log.info("EmailJmsConsumer: clientMsg processed and committed for message={}", message.id());

                    } catch (Exception procEx) {
                        log.error("Error processing clientMsg: {}", procEx.getMessage(), procEx);
                        try {
                            session.rollback();
                            log.info("EmailJmsConsumer: session rolled back");
                        } catch (Exception se) {
                            log.warn("Failed to rollback session: {}", se.getMessage(), se);
                        }
                    }

                } catch (Exception e) {
                    log.error("Unexpected error while handling clientMsg: {}", e.getMessage(), e);
                    try {
                        session.rollback();
                        log.info("EmailJmsConsumer: session rolled back after unexpected error");
                    } catch (Exception se) {
                        log.warn("Failed to rollback session after unexpected error: {}", se.getMessage(), se);
                    }
                }
            }
        }
    }

    private static void commit(ClientSession session, ClientMessage message) throws ActiveMQException {
        try {
            message.acknowledge();
            session.commit();
        } catch (ActiveMQException e) {
            log.warn("Failed to commit after acknowledge, attempting rollback: {}", e.getMessage(), e);
            try {
                session.rollback();
            } catch (Exception ex) {
                log.warn("Rollback after failed commit failed: {}", ex.getMessage(), ex);
            }
            throw e;
        }
    }
}
