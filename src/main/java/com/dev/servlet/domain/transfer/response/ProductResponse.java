package com.dev.servlet.domain.transfer.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

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
}
