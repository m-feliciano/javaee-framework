package com.dev.servlet.infrastructure.messaging.interfaces;

import com.dev.servlet.infrastructure.messaging.Message;

public interface MessageService {
    void send(Message message);

    void sendConfirmation(String email, String link);

    void sendWelcome(String email);
}
