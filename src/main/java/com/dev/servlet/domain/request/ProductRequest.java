package com.dev.servlet.domain.request;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductRequest(String id,
                             String name,
                             String description,
                             String url,
                             BigDecimal price,
                             CategoryRequest category) {
}