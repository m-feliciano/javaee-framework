package com.dev.servlet.application.transfer.response;

import lombok.Data;

@Data
public final class InventoryResponse {
    private String id;
    private Integer quantity;
    private String description;
    private String status;
    private ProductResponse product;

    public InventoryResponse(String id) {
        this.id = id;
    }
}
