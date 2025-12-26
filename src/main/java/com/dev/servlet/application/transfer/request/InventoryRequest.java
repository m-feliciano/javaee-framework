package com.dev.servlet.application.transfer.request;

import lombok.Builder;

import java.util.UUID;

@Builder
public record InventoryRequest(UUID id,
                               Integer quantity,
                               String description,
                               UserRequest user,
                               ProductRequest product) {
}
