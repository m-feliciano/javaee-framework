package com.dev.servlet.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for {@link com.dev.servlet.pojo.Product}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDto implements Serializable {
    private final Long id;
    private String name;
    private String description;
    private String url;
    private LocalDate registerDate;
    private BigDecimal price;
    private UserDto user;
    private String status;
    private CategoryDto category;

    public ProductDto(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public LocalDate getRegisterDate() {
        return registerDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public UserDto getUser() {
        return user;
    }

    public String getStatus() {
        return status;
    }

    public CategoryDto getCategory() {
        return category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setRegisterDate(LocalDate registerDate) {
        this.registerDate = registerDate;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCategory(CategoryDto category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "name = " + name + ", " +
                "description = " + description + ", " +
                "url = " + url + ", " +
                "registerDate = " + registerDate + ", " +
                "price = " + price + ", " +
                "user = " + user + ", " +
                "status = " + status + ", " +
                "category = " + category + ")";
    }
}