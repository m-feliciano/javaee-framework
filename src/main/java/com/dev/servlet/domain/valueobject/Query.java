package com.dev.servlet.domain.valueobject;

import lombok.Builder;

import java.util.Map;

@Builder
public record Query(Map<String, String> queries) {
    public String get(String key) {
        return has(key) ? queries.get(key) : null;
    }

    public boolean has(String key) {
        return queries != null && queries.containsKey(key);
    }
}
