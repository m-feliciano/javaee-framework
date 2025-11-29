package com.dev.servlet.application.transfer.request;

import lombok.Builder;

@Builder
public record InventoryRequest(String id,
                               Integer quantity,
                               String description,
                               UserRequest user,
                               ProductRequest product) {
}
