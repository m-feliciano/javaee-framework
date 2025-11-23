package com.dev.servlet.domain.records;

import lombok.Builder;

import java.util.Map;

@Builder
public record Query(Map<String, String> queries) {

    public String get(String key) {
        return queries != null ? queries.get(key) : null;
    }
}
