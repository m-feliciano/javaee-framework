package com.servletstack.application.port.out;

import com.servletstack.adapter.out.messaging.Message;

public interface MessagePort {
    void send(Message message);

    void sendConfirmation(String email, String link);

    void sendWelcome(String email);
}
