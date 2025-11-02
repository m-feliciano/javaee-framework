package com.dev.servlet.domain.service.internal;

import com.dev.servlet.core.util.JwtUtil;
import com.dev.servlet.domain.service.AuditService;
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

    @Inject
    public AuditServiceImpl(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    private void audit(String event, String token, String outcome, Object payload) {
        try {
            Map<String, Object> record = new HashMap<>();
            record.put("event", event);
            record.put("schemaVersion", "1.0");
            record.put("timestamp", Instant.now().toString());
            record.put("correlationId", MDC.get("correlationId"));
            record.put("outcome", outcome);
            if (token != null && !token.isBlank())
                record.put("userId", jwtUtil.getUserId(token));

            try {
                record.put("payload", payload);
            } catch (Exception e) {
                logger.warn("Failed to serialize payload", e);
            }
            // Log structured JSON. In production, route this logger to a centralized system (ELK/Cloud).
            logger.info(mapper.writeValueAsString(record));
        } catch (Exception e) {
            logger.error("Failed to write audit record", e);
        }
    }

    public void auditSuccess(String event, String token, AuditPayload<?, ?> payload) {
        audit(event, token, "success", payload);
    }

    public void auditFailure(String event, String token, AuditPayload<?, ?> payload) {
        audit(event, token, "failure", payload);
    }

    public void auditWarning(String event, String token, AuditPayload<?, ?> payload) {
        audit(event, token, "warning", payload);
    }
}
