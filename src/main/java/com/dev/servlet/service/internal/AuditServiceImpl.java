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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class AuditServiceImpl implements AuditService {

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
        try {
            Map<String, Object> record = new HashMap<>();
            record.put("event", event);
            record.put("schemaVersion", "1.0");
            record.put("timestamp", Instant.now().toString());
            record.put("correlationId", MDC.get("correlationId"));
            record.put("outcome", outcome);

            String userId = null;
            if (token != null && !token.isBlank()) {
                userId = jwtUtil.getUserId(token);
                record.put("userId", userId);
            }

            try {
                record.put("payload", payload);
            } catch (Exception e) {
                logger.warn("Failed to serialize payload", e);
            }

            logger.info(mapper.writeValueAsString(record));

            if (userId != null && payload instanceof AuditPayload<?, ?> auditPayload) {
                registerActivityLog(event, userId, outcome, auditPayload);
            }
        } catch (Exception e) {
            logger.error("Failed to write audit record", e);
        }
    }

    private void registerActivityLog(String event, String userId, ActivityStatus outcome, AuditPayload<?, ?> payload) {
        try {
            String requestJson = payload.input() != null ? mapper.writeValueAsString(payload.input()) : null;
            String responseJson = payload.output() != null ? mapper.writeValueAsString(payload.output()) : null;

            String startTime = MDC.get("requestStartTime");
            long startTimeMillis = Long.parseLong(startTime);

            UserActivityLog log = UserActivityLog.builder()
                    .userId(userId)
                    .action(event)
                    .status(outcome)
                    .requestPayload(requestJson)
                    .responsePayload(responseJson)
                    .correlationId(MDC.get("correlationId"))
                    .timestamp(new Date())
                    .httpMethod(getMetadata("httpMethod"))
                    .endpoint(getMetadata("endpoint"))
                    .ipAddress(getMetadata("ipAddress"))
                    .userAgent(getMetadata("userAgent"))
                    .entityType(getMetadata("entityType"))
                    .entityId(getMetadata("entityId"))
                    .errorMessage(getMetadata("errorMessage"))
                    .executionTimeMs(System.currentTimeMillis() - startTimeMillis)
                    .build();

            activityService.logActivity(log);
        } catch (Exception e) {
            logger.error("Failed to persist activity log", e);
        }
    }

    private String getMetadata(String key) {
        return MDC.get(key);
    }

    public void auditSuccess(String event, String token, AuditPayload<?, ?> payload) {
        audit(event, token, ActivityStatus.SUCCESS, payload);
    }

    public void auditFailure(String event, String token, AuditPayload<?, ?> payload) {
        audit(event, token, ActivityStatus.FAILED, payload);
    }

    public void auditWarning(String event, String token, AuditPayload<?, ?> payload) {
        audit(event, token, ActivityStatus.PENDING, payload);
    }
}
