package com.servletstack.application.transfer.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public final class ProductResponse {
    private UUID id;
    private String name;
    private String description;
    private String thumbUrl;
    private String status;
    private LocalDate registerDate;
    private BigDecimal price;
    private CategoryResponse category;

    public ProductResponse(UUID id) {
        this.id = id;
    }

    public ProductResponse(UUID id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public ProductResponse(UUID id, String name, BigDecimal price, String thumbUrl) {
        this(id, name, price);
        this.thumbUrl = thumbUrl;
    }

    @JsonIgnore
    public String getRegisterDateFormatted() {
        return registerDate != null ? registerDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

    @JsonIgnore
    public String getPriceFormatted() {
        if (price == null) return "";
        return NumberFormat.getCurrencyInstance(Locale.getDefault()).format(price);
    }
}
