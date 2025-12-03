package com.dev.servlet.shared.vo;

import lombok.Builder;

import java.util.Map;

@Builder
public record Query(Map<String, String> parameters) {
    public String get(String key) {
        return has(key) ? parameters.get(key) : null;
    }

    public boolean has(String key) {
        return parameters != null && parameters.containsKey(key);
    }
}
