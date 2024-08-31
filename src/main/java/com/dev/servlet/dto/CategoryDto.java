package com.dev.servlet.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link com.dev.servlet.pojo.Category}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryDto implements Serializable {
    private final Long id;
    private String name;
    private List<ProductDto> products;
    private String status;
    private UserDto user;

    public CategoryDto(Long id) {
        this.id = id;
    }

    public CategoryDto(Long id, String name, List<ProductDto> products, String status, UserDto user) {
        this.id = id;
        this.name = name;
        this.products = products;
        this.status = status;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<ProductDto> getProducts() {
        return products;
    }

    public String getStatus() {
        return status;
    }

    public UserDto getUser() {
        return user;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProducts(List<ProductDto> products) {
        this.products = products;
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
                "name = " + name + ", " +
                "products = " + products + ", " +
                "status = " + status + ", " +
                "user = " + user + ")";
    }
}