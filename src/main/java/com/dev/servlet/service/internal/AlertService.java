package com.dev.servlet.service.internal;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.OffsetDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class AlertService {

    public record Alert(String id, String status, String message, String createdAt) {
        public Alert(String status, String message) {
            this(UUID.randomUUID().toString(), status, message, OffsetDateTime.now().toString());
        }
    }

    private final Map<String, Deque<Alert>> store = new ConcurrentHashMap<>();
    private final int maxPerUser = 50;

    public void publish(String userId, String status, String message) {
        store.compute(userId, (k, dq) -> {
            if (dq == null)
                dq = new ArrayDeque<>();
            dq.addFirst(new Alert(status, message));

            while (dq.size() > maxPerUser)
                dq.removeLast();

            return dq;
        });
    }

    public List<Alert> list(String userId) {
        Deque<Alert> dq = store.get(userId);
        if (dq == null) return List.of();
        return new ArrayList<>(dq);
    }

    public void clear(String userId) {
        store.remove(userId);
    }
}

