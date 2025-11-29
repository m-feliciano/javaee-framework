package com.dev.servlet.application.transfer.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@AllArgsConstructor
@NoArgsConstructor
@Data
public final class ProductResponse {
    private String id;
    private String name;
    private String description;
    private String url;
    private String status;
    private LocalDate registerDate;
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

    public String getRegisterDateFormatted() {
        return registerDate != null ? registerDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

    public String getPriceFormatted() {
        if (price == null) return "";
        return NumberFormat.getCurrencyInstance(Locale.getDefault()).format(price);
    }
}
