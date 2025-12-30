package com.dev.servlet.adapter.in.messaging;

import com.dev.servlet.adapter.out.messaging.Message;
import com.dev.servlet.adapter.out.messaging.registry.MessageServiceRegistry;
import com.dev.servlet.infrastructure.config.Properties;
import com.dev.servlet.shared.util.CloneUtil;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.function.Consumer;

@Slf4j
@ApplicationScoped
@Named("sqsMessageConsumer")
public class SqsMessageConsumer {

    private SqsClient sqsClient;
    private String queueUrl;
    private volatile boolean running = true;

    @Inject
    private MessageServiceRegistry registry;

    public void onStartup(@Observes @Initialized(ApplicationScoped.class) Object init) {
        if ("sqs".equals(Properties.get("provider.broker"))) {
            log.info("SqsMessageConsumer: SQS provider configured, initializing SqsMessageConsumer");

            this.queueUrl = Properties.get("sqs.queue.url");
            this.sqsClient = SqsClient.builder()
                    .region(Region.of(Properties.get("sqs.region")))
                    .build();

            Thread.ofVirtual()
                    .name("sqs-consumer-polling-thread")
                    .unstarted(this::poll)
                    .start();
        } else {
            log.info("SqsMessageConsumer: SQS provider not configured, skipping SqsMessageConsumer initialization");
        }
    }

    @PreDestroy
    void shutdown() {
        log.info("Stopping SQS consumer");
        running = false;
        if (sqsClient != null) sqsClient.close();
    }

    private void poll() {
        var receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .waitTimeSeconds(20)
                .maxNumberOfMessages(5)
                .build();

        while (running) {
            try {
                ReceiveMessageResponse response = sqsClient.receiveMessage(receiveMessageRequest);

                for (software.amazon.awssdk.services.sqs.model.Message modelMsg : response.messages()) {
                    try {
                        String payload = modelMsg.body();
                        log.info("Received SQS message: {}", payload);
                        onReceive(payload);
                        log.debug("Acknowledging SQS message with receipt handle: {}", modelMsg.receiptHandle());

                        var deleteMessageRequest = DeleteMessageRequest.builder()
                                .queueUrl(queueUrl)
                                .receiptHandle(modelMsg.receiptHandle())
                                .build();
                        log.debug("Deleting SQS message with receipt handle: {}", modelMsg.receiptHandle());

                        sqsClient.deleteMessage(deleteMessageRequest);

                    } catch (Exception e) {
                        log.error("Error processing SQS message", e);
                    }
                }

            } catch (Exception e) {
                log.error("Polling error", e);
            }
        }
    }

    private void onReceive(String payload) {
        if (StringUtils.isBlank(payload)) {
            log.warn("Received blank SQS message, acknowledging");
            return;
        }

        Message message;
        Consumer<Message> handler;
        try {
            message = CloneUtil.fromJson(payload, Message.class);
            if (message == null || message.type() == null)
                throw new IllegalArgumentException("Deserialized message or message type is null");

            handler = registry.getConsumer(message.type());
            if (handler == null)
                throw new IllegalArgumentException("No @ consumer registered for type " + message.type());

        } catch (Exception e) {
            log.error("Failed to process message payload: {}", e.getMessage(), e);
            // malformed message, acknowledge to avoid retries
            return;
        }

        log.debug("Processing message id={} of type {}", message.id(), message.type());
        handler.accept(message);
        log.debug("Message id={} of type {} processed", message.id(), message.type());
    }
}
