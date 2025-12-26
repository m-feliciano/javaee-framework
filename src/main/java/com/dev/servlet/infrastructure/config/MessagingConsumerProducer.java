package com.dev.servlet.infrastructure.config;

import com.dev.servlet.application.port.out.AsyncMessagePort;
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
    public AsyncMessagePort produce(
            @Provider("sqs") Instance<AsyncMessagePort> sqsMessageProducerInstance,
            @Provider("jms") Instance<AsyncMessagePort> jmsMessageProducerInstance
    ) {
        final String provider = Properties.getOrDefault("provider.broker", "jms");

        if ("sqs".equalsIgnoreCase(provider)) {
            log.info("MessagePort: Using SqsMessageProducer as the messaging provider.");
            return sqsMessageProducerInstance.get();
        }

        if ("jms".equalsIgnoreCase(provider)) {
            log.info("MessagePort: Using jmsMQMessageProducer as the messaging provider.");
            return jmsMessageProducerInstance.get();
        }

        throw new IllegalStateException("No @ AsyncMessagePort for provider: " + provider);
    }
}
