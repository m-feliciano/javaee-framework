package com.dev.servlet.application.transfer.request;

import com.dev.servlet.domain.vo.BinaryPayload;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductRequest(String id,
                             String name,
                             String description,
                             BigDecimal price,
                             CategoryRequest category,
                             BinaryPayload payload // thumb payload
) {
    public ProductRequest(String id) {
        this(id, null, null, null, null, null);
    }
}
