package com.dev.servlet.infrastructure.messaging;

import com.dev.servlet.application.port.out.MessagePort;
import com.dev.servlet.domain.enums.MessageType;
import com.dev.servlet.infrastructure.messaging.config.MessageConfig;
import com.dev.servlet.infrastructure.messaging.factory.MessageFactory;
import com.dev.servlet.infrastructure.utils.CloneUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.ClientProducer;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;

import java.time.OffsetDateTime;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.dev.servlet.infrastructure.messaging.config.MessageConfig.EMAIL_EXCHANGE_QUEUE;

@Slf4j
@ApplicationScoped
@Named("messageProducer")
public class MessageProducer implements MessagePort {
    private MessageConfig.BrokerConfig config;
    private ClientSessionFactory factory;
    private ExecutorService executor;
    private final ConcurrentLinkedQueue<SessionProducerHolder> holders = new ConcurrentLinkedQueue<>();
    private final ThreadLocal<SessionProducerHolder> threadLocalHolder = new ThreadLocal<>();

    @PostConstruct
    public void init() {
        this.config = MessageConfig.createBrokerConfig(EMAIL_EXCHANGE_QUEUE);
        this.factory = MessageFactory.createSessionFactory(config.brokerUrl());
        int threads = Math.max(2, Runtime.getRuntime().availableProcessors());
        this.executor = Executors.newFixedThreadPool(threads);

        log.info("MessageProducer initialized with {} threads", threads);
    }

    @PreDestroy
    public void shutdown() {
        // Close all holders
        holders.forEach(h -> {
            try {
                h.close();
            } catch (Exception e) {
                log.warn("Failed to close SessionProducerHolder: {}", e.getMessage(), e);
            }
        });

        // Shutdown executor
        if (executor != null) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        // Close the factory
        if (factory != null) {
            try {
                factory.close();
            } catch (Exception e) {
                log.warn("Failed to close ClientSessionFactory: {}", e.getMessage(), e);
            }
        }
    }

    @Override
    public void send(Message message) {
        executor.submit(() -> enqueueMessage(message));
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

    private void enqueueMessage(Message message) {
        SessionProducerHolder holder = getOrCreateHolder();
        try {
            holder.send(message);
        } catch (Exception e) {
            log.error("Failed to send email message to queue for email {}: {}", message.toEmail(), e.getMessage(), e);
            try {
                holder.close();
            } catch (Exception ex) {
                log.warn("Failed to close faulty holder: {}", ex.getMessage(), ex);
            }
            threadLocalHolder.remove();
            holders.remove(holder);
        }
    }

    private SessionProducerHolder getOrCreateHolder() {
        SessionProducerHolder holder = threadLocalHolder.get();
        if (holder == null || holder.isClosed()) {
            holder = createHolder();
            threadLocalHolder.set(holder);
            holders.add(holder);
        }
        return holder;
    }

    private SessionProducerHolder createHolder() {
        try {
            ClientSession session = factory.createSession(
                    config.user(), config.pass(), false, false, false, false, 1);
            ClientProducer producer = session.createProducer(config.queueName());
            log.info("MessageProducer: created session/producer for thread {}", Thread.currentThread().getName());
            return new SessionProducerHolder(session, producer);

        } catch (Exception e) {
            log.error("Failed to create session/producer: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static final class SessionProducerHolder {
        private final ClientSession session;
        private final ClientProducer producer;
        @Getter
        private volatile boolean closed = false;

        SessionProducerHolder(ClientSession session, ClientProducer producer) {
            this.session = session;
            this.producer = producer;
        }

        synchronized void send(Message message) throws Exception {
            if (closed) throw new IllegalStateException("Holder closed");
            var clientMessage = session.createMessage(true);
            var bodyBuffer = clientMessage.getBodyBuffer();
            String json = CloneUtil.toJson(message);
            bodyBuffer.writeString(json);
            producer.send(clientMessage);

            try {
                session.commit();
            } catch (ActiveMQException e) {
                log.warn("Failed to commit after send, attempting rollback: {}", e.getMessage(), e);
                try {
                    session.rollback();
                } catch (Exception ex) {
                    log.warn("Rollback after failed commit failed: {}", ex.getMessage(), ex);
                }
                throw e;
            }
        }

        synchronized void close() {
            if (closed) return;
            closed = true;
            try {
                if (producer != null) {
                    try {
                        producer.close();
                    } catch (Exception ignore) {
                    }
                }
            } finally {
                if (session != null && !session.isClosed()) {
                    try {
                        session.close();
                    } catch (Exception ignore) {
                    }
                }
            }
        }
    }
}
