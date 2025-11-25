package com.dev.servlet.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public final class ProductResponse {
    private String id;
    private String name;
    private String description;
    private String url;
    private String status;
    private Date registerDate;
    private BigDecimal price;
    private CategoryResponse category;

    public ProductResponse(String id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public ProductResponse(String id, String name, BigDecimal price, String url) {
        this(id, name, price);
        this.url = url;
    }
}
