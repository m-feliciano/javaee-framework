package com.dev.servlet.domain.response;

import lombok.Data;

@Data
public final class InventoryResponse {
    private String id;
    private Integer quantity;
    private String description;
    private String status;
    private ProductResponse product;
}
