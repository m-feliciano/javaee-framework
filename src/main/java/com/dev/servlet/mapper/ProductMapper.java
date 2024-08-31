package com.dev.servlet.mapper;

import com.dev.servlet.dto.ProductDto;
import com.dev.servlet.pojo.Product;

public final class ProductMapper {
    private ProductMapper() {
    }

    /**
     * {@link ProductDto} from {@link Product}
     *
     * @param product
     * @return {@link ProductDto}
     */
    public static ProductDto from(Product product) {
        if (product == null) return null;
        ProductDto dto = new ProductDto(product.getId());
        dto.setName(product.getName());
        dto.setStatus(product.getStatus());
        dto.setRegisterDate(product.getRegisterDate());
        dto.setUrl(product.getUrl());
        dto.setUser(UserMapper.onlyId(product.getUser()));
        dto.setPrice(product.getPrice());
        dto.setDescription(product.getDescription());
        dto.setCategory(CategoryMapper.onlyId(product.getCategory()));
        return dto;
    }

    /**
     * {@link Product} from {@link ProductDto}
     *
     * @param dto
     * @return {@link Product}
     */
    public static Product from(ProductDto dto) {
        if (dto == null) return null;
        Product product = new Product(dto.getId());
        product.setName(dto.getName());
        product.setStatus(dto.getStatus());
        product.setRegisterDate(dto.getRegisterDate());
        product.setUrl(dto.getUrl());
        product.setPrice(dto.getPrice());
        product.setDescription(dto.getDescription());

        product.setUser(UserMapper.from(dto.getUser()));
        product.setCategory(CategoryMapper.from(dto.getCategory()));
        return product;
    }
}
