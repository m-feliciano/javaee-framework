package com.dev.servlet.adapter.in.messaging.consumer;

import com.dev.servlet.adapter.out.messaging.Message;
import com.dev.servlet.application.port.out.MessagePort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
@NoArgsConstructor
@ApplicationScoped
public class ConfirmationMessageConsumer implements Consumer<Message> {
    @Inject
    @Named("smtpEmailSender")
    private MessagePort messagePort;

    @Override
    public void accept(Message message) {
        log.info("ConfirmationConsumer received message id={}, to email={}", message.id(), message.toEmail());
        messagePort.sendConfirmation(message.toEmail(), message.link());
    }
}
