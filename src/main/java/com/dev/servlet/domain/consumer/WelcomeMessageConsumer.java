package com.dev.servlet.domain.consumer;

import com.dev.servlet.infrastructure.messaging.Message;
import com.dev.servlet.infrastructure.messaging.interfaces.MessageService;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
@Singleton
public class WelcomeMessageConsumer implements Consumer<Message> {

    @Inject
    @Named("emailSender")
    private MessageService messageService;

    @Override
    public void accept(Message message) {
        log.info("WelcomeMessageConsumer received message for ID={}, to email={}", message.id(), message.toEmail());
        messageService.sendWelcome(message.toEmail(), message.link());
        log.info("Sending welcome to {} with link {}", message.toEmail(), message.link());
    }
}
