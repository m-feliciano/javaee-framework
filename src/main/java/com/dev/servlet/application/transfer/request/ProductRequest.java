package com.dev.servlet.application.transfer.request;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductRequest(String id,
                             String name,
                             String description,
                             String url,
                             BigDecimal price,
                             CategoryRequest category) {

    public ProductRequest(String id) {
        this(id, null, null, null, null, null);
    }
}
