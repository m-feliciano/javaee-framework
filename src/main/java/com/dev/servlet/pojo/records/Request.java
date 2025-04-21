package com.dev.servlet.pojo.records;

import java.util.List;
import java.util.Optional;

/**
 * This record represents an internal request.
 *
 * @since 1.4
 */
public record Request(
        String endpoint,
        String method,
        List<KeyPair> body,
        String token,
        Query query,
        int retry
) {
    public static Request of(String endpoint, String method, List<KeyPair> body, String token, Query query, int retry) {
        return new Request(endpoint, method, body, token, query, retry);
    }

    public String getParameter(String name) {
        return getParam(name)
                .map(value -> ((String) value).trim())
                .orElse(null);
    }

    private Optional<Object> getParam(String name) {
        return body.stream()
                .filter(p -> p.key().equalsIgnoreCase(name))
                .map(KeyPair::getValue)
                .findFirst();
    }

    public String id() {
        return getParameter("id");
    }
}