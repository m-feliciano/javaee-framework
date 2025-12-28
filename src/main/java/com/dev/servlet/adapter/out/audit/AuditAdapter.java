package com.dev.servlet.adapter.out.audit;

import com.dev.servlet.application.port.in.activity.LogUserActivityUseCase;
import com.dev.servlet.application.port.out.audit.AuditPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.domain.entity.enums.ActivityStatus;
import com.dev.servlet.shared.util.CloneUtil;
import com.dev.servlet.shared.vo.AuditPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.RequestContextController;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@ApplicationScoped
public class AuditAdapter implements AuditPort {
    private final static AtomicLong counter = new AtomicLong(0);

    private static final List<String> mdcKeys = List.of(
            "startedAt",
            "correlationId",
            "httpMethod",
            "endpoint",
            "ipAddress",
            "userAgent",
            "entityType",
            "entityId",
            "errorMessage"
    );

    @Inject
    private LogUserActivityUseCase userActivity;
    @Inject
    private AuthenticationPort auth;
    @Inject
    private RequestContextController requestContextController;

    @Override
    public void success(String event, String token, Object payload) {
        audit(event, token, ActivityStatus.SUCCESS, payload);
    }

    @Override
    public void failure(String event, String token, Object payload) {
        audit(event, token, ActivityStatus.FAILED, payload);
    }

    @Override
    public void warning(String event, String token, Object payload) {
        audit(event, token, ActivityStatus.WARNING, payload);
    }

    @Override
    public void info(String event, String token, Object payload) {
        audit(event, token, ActivityStatus.INFO, payload);
    }

    private void audit(String event, String token, ActivityStatus outcome, Object payload) {
        HashMap<String, Object> metadata = new HashMap<>();
        metadata.put("schemaVersion", "1.1");
        metadata.put("event", event);
        metadata.put("outcome", outcome);
        metadata.put("timestamp", Instant.now().toString());
        loadFromMdc(metadata);

        Thread.ofVirtual()
                .name("audit-logger-counter-" + counter.incrementAndGet())
                .start(() -> {
            final String threadName = Thread.currentThread().getName();
            log.debug("[Thread: {}] Logging audit event: {}", threadName, event);

            try {
                requestContextController.activate();

                UUID userId = null;
                if (token != null && !token.isBlank()) {
                    try {
                        userId = auth.extractUserId(token);
                    } catch (Exception ignored) {
                    }

                    metadata.put("userId", userId);
                }

                try {
                    metadata.put("payload", payload);
                } catch (Exception e) {
                    log.warn("[Thread: {}] Failed to serialize audit payload", threadName, e);
                }

                // [DEBUG only] log the metadata
                log.debug(CloneUtil.toJson(metadata));

                if (userId != null && payload instanceof AuditPayload<?, ?> auditPayload) {
                    userActivity.logActivity(userId, outcome, auditPayload, metadata);
                    log.debug("[Thread: {}] Activity log registered for userId: {}", threadName, userId);
                }

            } catch (Exception e) {
                log.error("[Thread: {}] Failed to log audit event", threadName, e);
            } finally {
                requestContextController.deactivate();
                log.debug("[Thread: {}] Audit logging thread completed for event: {}", threadName, event);
            }
        });
    }

    private void loadFromMdc(Map<String, Object> record) {
        for (String key : mdcKeys) {
            String value = MDC.get(key);
            if (value != null) {
                record.put(key, value);
            }
        }
    }
}
