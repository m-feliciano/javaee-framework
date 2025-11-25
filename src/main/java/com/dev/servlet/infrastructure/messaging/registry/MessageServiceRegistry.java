package com.dev.servlet.infrastructure.messaging.registry;

import com.dev.servlet.domain.consumer.ChangeEmailMessageConsumer;
import com.dev.servlet.domain.consumer.ConfirmationMessageConsumer;
import com.dev.servlet.domain.consumer.WelcomeMessageConsumer;
import com.dev.servlet.domain.enumeration.MessageType;
import com.dev.servlet.infrastructure.messaging.Message;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Singleton
public class MessageServiceRegistry {

    private final Map<MessageType, Consumer<Message>> registry = new HashMap<>();

    @Inject
    public MessageServiceRegistry(ConfirmationMessageConsumer confirmationMessageConsumer,
                                  ChangeEmailMessageConsumer changeEmailMessageConsumer,
                                  WelcomeMessageConsumer welcomeMessageConsumer) {
        registerService(MessageType.CONFIRMATION, confirmationMessageConsumer);
        registerService(MessageType.CHANGE_EMAIL, changeEmailMessageConsumer);
        registerService(MessageType.WELCOME, welcomeMessageConsumer);
    }

    public Consumer<Message> getConsumer(MessageType type) {
        return registry.get(type);
    }

    public void registerService(MessageType type, Consumer<Message> consumer) {
        registry.put(type, consumer);
    }
}
