package com.servletstack.application.port.out.alert;

import com.servletstack.application.transfer.Alert;

import java.util.List;
import java.util.UUID;

public interface AlertPort {
    List<Alert> list(UUID userId);

    void clear(UUID userId);

    void publish(UUID userId, String status, String message);
}
