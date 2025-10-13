package com.dev.servlet.domain.service;

import com.dev.servlet.domain.service.internal.AuditPayload;

public interface AuditService {
    void auditSuccess(String event, String token, AuditPayload<?, ?> payload);

    void auditFailure(String event, String token, AuditPayload<?, ?> payload);

    void auditWarning(String event, String token, AuditPayload<?, ?> payload);
}
