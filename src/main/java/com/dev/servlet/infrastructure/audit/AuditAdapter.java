package com.dev.servlet.infrastructure.audit;

import com.dev.servlet.application.port.out.AuditPort;
import com.dev.servlet.application.port.out.AuthenticationPort;
import com.dev.servlet.application.usecase.activity.LogUserActivityUseCase;
import com.dev.servlet.domain.entity.UserActivityLog;
import com.dev.servlet.domain.entity.enums.ActivityStatus;
import com.dev.servlet.infrastructure.utils.CloneUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.control.RequestContextController;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@ApplicationScoped
public class AuditAdapter implements AuditPort {
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
    private LogUserActivityUseCase activityService;
    @Inject
    private AuthenticationPort authenticationPort;
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
        metadata.put("schemaVersion", "1.0");
        metadata.put("event", event);
        metadata.put("outcome", outcome);
        metadata.put("timestamp", Instant.now().toString());
        loadFromMdc(metadata);
        Thread.ofVirtual().start(() -> {
            final String threadName = Thread.currentThread().getName();
            try {
                requestContextController.activate();
                log.debug("[Thread: {}] Logging audit event: {}", threadName, event);
                String userId = null;
                if (token != null && !token.isBlank()) {
                    try {
                        userId = authenticationPort.extractUserId(token);
                    } catch (Exception ignored) {
                    }
                    metadata.put("userId", userId);
                }
                try {
                    metadata.put("payload", payload);
                } catch (Exception e) {
                    log.warn("[Thread: {}] Failed to serialize audit payload", threadName, e);
                }
                log.info(CloneUtil.toJson(metadata));
                if (userId != null && payload instanceof AuditPayload<?, ?> auditPayload) {
                    registerActivityLog(userId, outcome, auditPayload, metadata);
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

    private void registerActivityLog(String userId,
                                     ActivityStatus outcome,
                                     AuditPayload<?, ?> payload,
                                     HashMap<String, Object> metadata) {
        try {
            String requestJson = CloneUtil.toJson(payload.input());
            String responseJson = CloneUtil.toJson(payload.output());
            Long executionTimeMs = null;
            if (metadata.get("startedAt") != null) {
                executionTimeMs = System.currentTimeMillis() - Long.parseLong(metadata.get("startedAt").toString());
            }
            UserActivityLog log = UserActivityLog.builder()
                    .userId(userId)
                    .action(metadata.get("event").toString())
                    .status(outcome)
                    .timestamp(LocalDateTime.now())
                    .executionTimeMs(executionTimeMs)
                    .requestPayload(requestJson)
                    .responsePayload(responseJson)
                    .correlationId((String) metadata.get("correlationId"))
                    .httpMethod((String) metadata.get("httpMethod"))
                    .endpoint((String) metadata.get("endpoint"))
                    .ipAddress((String) metadata.get("ipAddress"))
                    .userAgent((String) metadata.get("userAgent"))
                    .entityType((String) metadata.get("entityType"))
                    .entityId((String) metadata.get("entityId"))
                    .errorMessage((String) metadata.get("errorMessage"))
                    .build();
            activityService.logActivity(log);
        } catch (Exception e) {
            log.error("Failed to persist activity log", e);
        }
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
