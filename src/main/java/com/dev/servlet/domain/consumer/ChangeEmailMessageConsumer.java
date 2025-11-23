package com.dev.servlet.domain.consumer;

import com.dev.servlet.infrastructure.messaging.Message;
import com.dev.servlet.infrastructure.messaging.interfaces.MessageService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.function.Consumer;

@Slf4j
@Singleton
public class ChangeEmailMessageConsumer implements Consumer<Message> {

    @Inject
    @Named("emailSender")
    private MessageService messageService;

    @Override
    public void accept(Message message) {
        log.info("ChangeEmailMessageConsumer received message for userId={}, email={}", message.userId(), message.email());
        messageService.send(message);
        log.info("Sending change confirmation email to {} with link {}", message.email(), message.link());
    }
}
