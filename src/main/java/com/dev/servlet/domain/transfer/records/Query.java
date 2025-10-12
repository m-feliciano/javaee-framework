package com.dev.servlet.domain.transfer.records;

import lombok.Builder;

import java.util.Map;

@Builder
public record Query(Map<String, String> queries) {
}
