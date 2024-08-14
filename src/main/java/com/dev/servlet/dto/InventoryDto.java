package com.dev.servlet.dto;

import com.dev.servlet.domain.Inventory;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * DTO for {@link Inventory}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InventoryDto implements Serializable {
    private final Long id;
    private Integer quantity;
    private String description;
    private ProductDto product;
    private String status;
    private UserDto user;

    public InventoryDto(Long id) {
        this.id = id;
    }

    public InventoryDto(Long id, Integer quantity, String description, ProductDto product, String status, UserDto user) {
        this.id = id;
        this.quantity = quantity;
        this.description = description;
        this.product = product;
        this.status = status;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getDescription() {
        return description;
    }

    public ProductDto getProduct() {
        return product;
    }

    public String getStatus() {
        return status;
    }

    public UserDto getUser() {
        return user;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProduct(ProductDto product) {
        this.product = product;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "quantity = " + quantity + ", " +
                "description = " + description + ", " +
                "product = " + product + ", " +
                "status = " + status + ", " +
                "user = " + user + ")";
    }
}