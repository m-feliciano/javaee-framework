package com.dev.servlet.adapter.out.messaging.producer;

import com.dev.servlet.adapter.out.messaging.Message;
import com.dev.servlet.application.port.out.MessagePort;
import com.dev.servlet.domain.enums.MessageType;
import com.dev.servlet.infrastructure.config.Properties;
import com.dev.servlet.shared.util.CloneUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.time.OffsetDateTime;

@Slf4j
@ApplicationScoped
@Named("sqsMessageProducer")
public class SqsMessageProducer implements MessagePort {

    private SqsClient sqsClient;
    private String queueUrl;

    @PostConstruct
    public void init() {
        if ("sqs".equals(Properties.get("provider.broker"))) {
            log.info("SqsMessageProducer: SQS provider configured, initializing SqsMessageProducer");
            String region = Properties.getOrDefault("sqs.region", "us-east-1");

            this.queueUrl = Properties.get("sqs.queue.url");
            this.sqsClient = SqsClient.builder()
                    .region(Region.of(region))
                    .build();

            log.info("SQS MessageProducer started for queue {}", queueUrl);
        } else {
            log.info("SqsMessageProducer: SQS provider not configured, skipping SqsMessageProducer initialization");
        }
    }

    @Override
    public void send(Message message) {
        try {
            String json = CloneUtil.toJson(message);
            log.debug("Sending message to SQS queue {}: {}", queueUrl, json);

            var sendMessageRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageDeduplicationId(message.id())
                    .messageGroupId(message.toEmail())
                    .messageBody(json)
                    .build();
            sqsClient.sendMessage(sendMessageRequest);

            log.info("Message sent to SQS queue {} for email={}", queueUrl, message.toEmail());

        } catch (Exception e) {
            log.error("Failed to send SQS message", e);
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
    void shutdown() {
        log.info("Shutting down SQS MessageProducer");
        if (sqsClient != null) sqsClient.close();
    }
}
