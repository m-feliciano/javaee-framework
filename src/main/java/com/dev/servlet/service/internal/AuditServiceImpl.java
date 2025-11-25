package com.dev.servlet.service.internal;

import com.dev.servlet.core.util.JwtUtil;
import com.dev.servlet.domain.model.UserActivityLog;
import com.dev.servlet.domain.model.enums.ActivityStatus;
import com.dev.servlet.service.AuditService;
import com.dev.servlet.service.UserActivityService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.enterprise.context.control.RequestContextController;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class AuditServiceImpl implements AuditService {

    private final List<String> mdcKeys = List.of(
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
    private RequestContextController requestContextController;

    private static final Logger logger = LoggerFactory.getLogger(AuditServiceImpl.class);

    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private final JwtUtil jwtUtil;
    private final UserActivityService activityService;

    @Inject
    public AuditServiceImpl(JwtUtil jwtUtil, UserActivityService activityService) {
        this.jwtUtil = jwtUtil;
        this.activityService = activityService;
    }

    private void audit(String event, String token, ActivityStatus outcome, Object payload) {
        HashMap<String, Object> metadata = new HashMap<>();
        metadata.put("schemaVersion", "1.0");
        metadata.put("event", event);
        metadata.put("outcome", outcome);
        metadata.put("timestamp", Instant.now().toString());
        loadFromMdc(metadata);

        Thread.ofVirtual()
                .name("audit-logger-%d")
                .start(() -> {
                    final String threadName = Thread.currentThread().getName();
                    try {
                        requestContextController.activate();
                        logger.debug("[Thread: {}] Logging audit event: {}", threadName, event);

                        String userId = null;
                        if (token != null && !token.isBlank()) {
                            try {
                                userId = jwtUtil.getUserId(token);
                            } catch (Exception ignored) {
                            }

                            metadata.put("userId", userId);
                        }

                        try {
                            metadata.put("payload", payload);
                        } catch (Exception e) {
                            logger.warn("[Thread: {}] Failed to serialize audit payload", threadName, e);
                        }

                        logger.info(mapper.writeValueAsString(metadata));

                        if (userId != null && payload instanceof AuditPayload<?, ?> auditPayload) {
                            registerActivityLog(userId, outcome, auditPayload, metadata);
                            logger.debug("[Thread: {}] Activity log registered for userId: {}", threadName, userId);
                        }
                    } catch (Exception e) {
                        logger.error("[Thread: {}] Failed to log audit event", threadName, e);
                    } finally {
                        requestContextController.deactivate();
                        logger.debug("[Thread: {}] Audit logging thread completed for event: {}", threadName, event);
                    }
                });
    }

    private void registerActivityLog(String userId,
                                     ActivityStatus outcome,
                                     AuditPayload<?, ?> payload,
                                     HashMap<String, Object> metadata) {
        try {
            String requestJson = payload.input() != null ? mapper.writeValueAsString(payload.input()) : null;
            String responseJson = payload.output() != null ? mapper.writeValueAsString(payload.output()) : null;

            Long executionTimeMs = null;
            if (metadata.get("startedAt") != null) {
                executionTimeMs = System.currentTimeMillis() - Long.parseLong(metadata.get("startedAt").toString());
            }

            UserActivityLog log = UserActivityLog.builder()
                    .userId(userId)
                    .action(metadata.get("event").toString())
                    .status(outcome)
                    .timestamp(new Date())
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
            logger.error("Failed to persist activity log", e);
        }
    }

    @Override
    public void auditSuccess(String event, String token, AuditPayload<?, ?> payload) {
        audit(event, token, ActivityStatus.SUCCESS, payload);
    }

    @Override
    public void auditFailure(String event, String token, AuditPayload<?, ?> payload) {
        audit(event, token, ActivityStatus.FAILED, payload);
    }

    @Override
    public void auditWarning(String event, String token, AuditPayload<?, ?> payload) {
        audit(event, token, ActivityStatus.WARNING, payload);
    }

    @Override
    public void auditInfo(String event, String token, AuditPayload<?, ?> payload) {
        audit(event, token, ActivityStatus.INFO, payload);
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
