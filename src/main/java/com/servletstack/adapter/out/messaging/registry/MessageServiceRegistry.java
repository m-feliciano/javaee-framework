package com.servletstack.adapter.out.messaging.registry;

import com.servletstack.adapter.in.messaging.consumer.ChangeEmailMessageConsumer;
import com.servletstack.adapter.in.messaging.consumer.ConfirmationMessageConsumer;
import com.servletstack.adapter.in.messaging.consumer.WelcomeMessageConsumer;
import com.servletstack.adapter.out.messaging.Message;
import com.servletstack.domain.enums.MessageType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@NoArgsConstructor
@ApplicationScoped
public class MessageServiceRegistry {
    private final Map<MessageType, Consumer<Message>> registry = new ConcurrentHashMap<>();

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

    private void registerService(MessageType type, Consumer<Message> consumer) {
        registry.put(type, consumer);
    }
}
