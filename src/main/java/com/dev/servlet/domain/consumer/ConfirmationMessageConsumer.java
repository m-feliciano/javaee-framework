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
public class ConfirmationMessageConsumer implements Consumer<Message> {

    @Inject
    @Named("emailSender")
    private MessageService messageService;

    @Override
    public void accept(Message message) {
        log.info("ConfirmationConsumer received message id={}, to email={}", message.id(), message.toEmail());
        messageService.sendConfirmation(message.toEmail(), message.link());
        log.info("Sending confirmation to {} with link {}", message.toEmail(), message.link());
    }
}
