package com.servletstack.application.transfer.response;

import lombok.Data;

import java.util.UUID;

@Data
public final class InventoryResponse {
    private UUID id;
    private Integer quantity;
    private String description;
    private String status;
    private ProductResponse product;

    public InventoryResponse(UUID id) {
        this.id = id;
    }
}
