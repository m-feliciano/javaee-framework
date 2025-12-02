package com.dev.servlet.infrastructure.alert;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@ApplicationScoped
public class AlertService {

    private static final Map<String, Deque<Alert>> store = new ConcurrentHashMap<>();

    @Inject
    private AlertWebSocket ws;

    public void publish(String userId, String status, String message) {
        store.computeIfAbsent(userId, k -> new ConcurrentLinkedDeque<>());
        Deque<Alert> alerts = store.get(userId);

        int maxAlertsPerUser = 30;
        if (alerts.size() >= maxAlertsPerUser) alerts.removeFirst();

        Alert alert = new Alert(status, message);
        alerts.addLast(alert);
        ws.push(userId, alert);
    }

    /**
     * List alerts for a user.
     *
     * @param userId ID of the user
     * @return List of alerts for the user
     * @deprecated Use WebSocket to receive alerts in real-time.
     */
    @Deprecated
    public List<Alert> list(String userId) {
        Deque<Alert> alerts = store.get(userId);
        if (alerts == null) return List.of();
        return new ArrayList<>(alerts);
    }

    public void clear(String userId) {
        store.remove(userId);
    }

    public record Alert(String id, String status, String message, String createdAt) {
        public Alert(String status, String message) {
            this(UUID.randomUUID().toString(), status, message, OffsetDateTime.now().toString());
        }
    }
}
