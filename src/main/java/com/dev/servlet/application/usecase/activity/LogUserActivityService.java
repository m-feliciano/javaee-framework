package com.dev.servlet.application.usecase.activity;

import com.dev.servlet.application.port.in.activity.LogUserActivityUseCase;
import com.dev.servlet.application.port.out.activity.UserActivityLogRepositoryPort;
import com.dev.servlet.domain.entity.UserActivityLog;
import com.dev.servlet.domain.entity.enums.ActivityStatus;
import com.dev.servlet.shared.util.CloneUtil;
import com.dev.servlet.shared.vo.AuditPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class LogUserActivityService implements LogUserActivityUseCase {

    @Inject
    private UserActivityLogRepositoryPort repository;

    @Override
    public void logActivity(UUID userId,
                            ActivityStatus outcome,
                            AuditPayload<?, ?> payload,
                            HashMap<String, Object> metadata) {
        log.debug("Logging user activity for userId: {}, action: {}, status: {}", userId, metadata.get("event"), outcome);

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
        repository.save(log);
    }
}
