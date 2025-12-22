package com.dev.servlet.infrastructure.config;

import com.dev.servlet.application.port.out.MessagePort;
import com.dev.servlet.infrastructure.annotations.Provider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class MessagingConsumerProducer {

    @Produces
    @Default
    @ApplicationScoped
    public MessagePort produce(
            @Provider("sqs") Instance<MessagePort> sqsMessageConsumerInstance,
            @Provider("jms") Instance<MessagePort> jmsMessageConsumerInstance
    ) {
        final String provider = Properties.getOrDefault("provider.broker", "jms");

        if ("sqs".equalsIgnoreCase(provider)) {
            log.info("MessagePort: Using SqsMessageProducer as the messaging provider.");
            return sqsMessageConsumerInstance.get();
        }

        if ("jms".equalsIgnoreCase(provider)) {
            log.info("MessagePort: Using jmsMQMessageProducer as the messaging provider.");
            return jmsMessageConsumerInstance.get();
        }

        throw new IllegalStateException("No @ MessagePort for provider: " + provider);
    }
}
