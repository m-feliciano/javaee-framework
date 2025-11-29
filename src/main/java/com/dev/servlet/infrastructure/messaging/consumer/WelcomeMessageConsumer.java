package com.dev.servlet.infrastructure.messaging.consumer;

import com.dev.servlet.application.port.out.MessagePort;
import com.dev.servlet.infrastructure.messaging.Message;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
@NoArgsConstructor
@ApplicationScoped
public class WelcomeMessageConsumer implements Consumer<Message> {
    @Inject
    @Named("emailSender")
    private MessagePort messagePort;

    @Override
    public void accept(Message message) {
        log.info("WelcomeMessageConsumer received message for ID={}, to email={}", message.id(), message.toEmail());
        messagePort.sendWelcome(message.toEmail());
    }
}
