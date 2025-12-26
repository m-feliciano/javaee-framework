package com.dev.servlet.application.transfer.request;

import com.dev.servlet.domain.vo.BinaryPayload;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ProductRequest(UUID id,
                             String name,
                             String description,
                             BigDecimal price,
                             CategoryRequest category,
                             BinaryPayload payload // thumb payload
) {
    public ProductRequest(UUID id) {
        this(id, null, null, null, null, null);
    }
}
