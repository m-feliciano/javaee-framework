package com.dev.servlet.service;

import com.dev.servlet.service.internal.AuditPayload;

public interface AuditService {
    void auditSuccess(String event, String token, AuditPayload<?, ?> payload);

    void auditFailure(String event, String token, AuditPayload<?, ?> payload);

    void auditWarning(String event, String token, AuditPayload<?, ?> payload);
}
