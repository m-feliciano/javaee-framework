package com.dev.servlet.infrastructure.messaging;

import com.dev.servlet.core.util.CloneUtil;
import com.dev.servlet.domain.enumeration.MessageType;
import com.dev.servlet.infrastructure.messaging.config.MessageConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.artemis.api.core.ActiveMQBuffer;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.QueueConfiguration;
import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ClientConsumer;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.activemq.artemis.api.core.client.ServerLocator;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static com.dev.servlet.infrastructure.messaging.config.MessageConfig.EMAIL_EXCHANGE_QUEUE;

@Slf4j
@ApplicationScoped
@Named("messageConsumer")
public class MessageConsumer {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private MessageConfig.BrokerConfig config;
    private ClientSessionFactory sessionFactory;

    @Inject
    private MessageServiceRegistry messageServiceRegistry;

    public void onStartup(@Observes @Initialized(ApplicationScoped.class) Object init) {
        this.config = MessageConfig.createBrokerConfig(EMAIL_EXCHANGE_QUEUE);
        initSessionFactory();
        startConsumerThread();
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
    }

    private void initSessionFactory() {
        try {
            ServerLocator locator = ActiveMQClient.createServerLocator(config.brokerUrl());
            this.sessionFactory = locator.createSessionFactory();
            log.info("EmailJmsConsumer: created session factory for brokerUrl={}", config.brokerUrl());
        } catch (Exception e) {
            log.error("Error initializing JMS ConnectionFactory: {}", e.getMessage(), e);
            throw new IllegalStateException("Failed to initialize JMS ConnectionFactory", e);
        }
    }

    private void startConsumerThread() {
        executor.submit(() -> {
            log.info("EmailJmsConsumer: consumer thread started");
            try (ClientSession session = createSession()) {
                ensureQueue(session);
                session.start();
                log.info("EmailJmsConsumer: session started");

                consumeMessages(session, (message) -> {
                    try {
                        MessageType messageType = MessageType.of(message.type());
                        var consumer = messageServiceRegistry.getConsumer(messageType);
                        consumer.accept(message);

                    } catch (Exception e) {
                        log.error("Error in message consumer: {}", e.getMessage(), e);
                    }
                });
            } catch (Exception e) {
                log.error("Error in EmailJmsConsumer thread: {}", e.getMessage(), e);
                Thread.currentThread().interrupt();
            }
        });
    }

    private ClientSession createSession() throws Exception {
        ClientSession session = sessionFactory.createSession(
                config.user(), config.pass(), false, true, false, false, 1);
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
            log.warn("EmailJmsConsumer: createQueue() warning: {}", e.getMessage(), e);
        }
    }

    private void consumeMessages(ClientSession session, Consumer<Message> then) throws Exception {
        try (ClientConsumer consumer = session.createConsumer(EMAIL_EXCHANGE_QUEUE)) {
            log.info("EmailJmsConsumer: consumer created for queue {}", EMAIL_EXCHANGE_QUEUE);

            while (!Thread.currentThread().isInterrupted()) {
                ClientMessage clientMsg = consumer.receive(1000);
                if (clientMsg == null) continue;

                try {
                    ActiveMQBuffer buf = clientMsg.getBodyBuffer();
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

                    Message message = CloneUtil.fromJson(body, Message.class);
                    then.accept(message);

                    commit(session, clientMsg);
                    log.info("EmailJmsConsumer: clientMsg processed and committed for email={}", message.email());

                } catch (Exception e) {
                    log.error("Error processing clientMsg: {}", e.getMessage(), e);
                    try {
                        session.rollback();
                        log.info("EmailJmsConsumer: session rolled back");
                    } catch (Exception se) {
                        log.warn("Failed to rollback session: {}", se.getMessage(), se);
                    }
                }
            }
        }
    }

    private static void commit(ClientSession session, ClientMessage message) throws ActiveMQException {
        message.acknowledge();
        session.commit();
    }
}
