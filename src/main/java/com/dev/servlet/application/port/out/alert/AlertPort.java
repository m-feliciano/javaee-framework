package com.dev.servlet.application.port.out.alert;

import com.dev.servlet.application.transfer.Alert;

import java.util.List;

public interface AlertPort {
    List<Alert> list(String userId);

    void clear(String userId);

    void publish(String userId, String status, String message);
}
