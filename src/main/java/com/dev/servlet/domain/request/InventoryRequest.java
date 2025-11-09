package com.dev.servlet.domain.request;

import lombok.Builder;

@Builder
public record InventoryRequest(String id,
                               Integer quantity,
                               String description,
                               UserRequest user,
                               ProductRequest product) {
}
