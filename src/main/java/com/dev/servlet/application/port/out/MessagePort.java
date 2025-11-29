package com.dev.servlet.application.port.out;

import com.dev.servlet.infrastructure.messaging.Message;

public interface MessagePort {
    void send(Message message);

    void sendConfirmation(String email, String link);

    void sendWelcome(String email);
}
