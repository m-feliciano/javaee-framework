package com.dev.servlet.infrastructure.messaging;

import com.dev.servlet.core.util.CloneUtil;
import com.dev.servlet.domain.enumeration.MessageType;
import com.dev.servlet.infrastructure.messaging.config.MessageConfig;
import com.dev.servlet.infrastructure.messaging.interfaces.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ClientProducer;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.activemq.artemis.api.core.client.ServerLocator;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.time.OffsetDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.dev.servlet.infrastructure.messaging.config.MessageConfig.EMAIL_EXCHANGE_QUEUE;

@Slf4j
@ApplicationScoped
@Named("messageProducer")
public class MessageProducer implements MessageService {
    private ExecutorService executor;
    private MessageConfig.BrokerConfig config;
    private ClientSessionFactory factory;

    @PostConstruct
    public void init() {
        this.executor = Executors.newFixedThreadPool(4, r -> {
            Thread t = new Thread(r);
            t.setName("msg-producer-" + t.getId());
            return t;
        });

        this.config = MessageConfig.createBrokerConfig(EMAIL_EXCHANGE_QUEUE);

        try {
            ServerLocator locator = ActiveMQClient.createServerLocator(config.brokerUrl());
            this.factory = locator.createSessionFactory();
        } catch (Exception e) {
            log.error("Error initializing JMS ConnectionFactory: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("Executor termination interrupted: {}", e.getMessage(), e);
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void send(Message message) {
        executor.submit(() -> sendEmail(message));
    }

    @Override
    public void sendConfirmation(String email, String link) {
        String createdAt = OffsetDateTime.now().toString();
        send(new Message(null, MessageType.CONFIRMATION.type, email, createdAt, link));
    }

    @Override
    public void sendWelcome(String userId, String email, String link) {
        String createdAt = OffsetDateTime.now().toString();
        send(new Message(userId, MessageType.WELCOME.type, email, createdAt, link));
    }

    private void sendEmail(Message message) {
        try (var session = factory.createSession(
                config.user(), config.pass(), false, false, false, false, 1)) {

            session.start();
            ClientProducer producer = session.createProducer(config.queueName());

            var clientMessage = session.createMessage(true);
            var bodyBuffer = clientMessage.getBodyBuffer();

            String json = CloneUtil.toJson(message);
            bodyBuffer.writeString(json);
            producer.send(clientMessage);
            session.commit();

        } catch (Exception e) {
            log.error("Failed to send email message to queue for email {}: {}", message.email(), e.getMessage(), e);
        }
    }
}
