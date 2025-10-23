package com.dev.servlet.domain.service.internal;

import java.util.Map;

/**
 * @param metadata optional
 */
public record AuditPayload<I, R>(I input, R output, Map<String, Object> metadata) {

    public AuditPayload(I input, R output) {
        this(input, output, null);
    }

    public static <R, S> AuditPayload<R, S> of(R req, S res) {
        return new AuditPayload<>(req, res);
    }
}
