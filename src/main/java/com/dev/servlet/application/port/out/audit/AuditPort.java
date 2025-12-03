package com.dev.servlet.application.port.out.audit;

public interface AuditPort {
    void success(String event, String token, Object payload);

    void failure(String event, String token, Object payload);

    void warning(String event, String token, Object payload);

    void info(String event, String token, Object payload);
}
