package com.dev.servlet.shared.vo;

import lombok.Builder;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Builder
public record Query(Map<String, String> parameters) {
    public String get(String key) {
        return has(key) ? parameters.get(key) : null;
    }

    public boolean has(String key) {
        return parameters != null && parameters.containsKey(key);
    }

    @NotNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (parameters != null) {
            parameters.forEach((key, value) -> sb.append(key).append("=").append(value).append(", "));
            if (!sb.isEmpty()) {
                sb.setLength(sb.length() - 2); // Remove trailing comma and space
            }
        }
        return sb.toString();
    }
}
