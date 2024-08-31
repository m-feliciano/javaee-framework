package com.dev.servlet.mapper;

import com.dev.servlet.dto.CategoryDto;
import com.dev.servlet.pojo.Category;

public final class CategoryMapper {
    private CategoryMapper() {
    }

    /**
     * {@link CategoryDto} from {@link Category}
     *
     * @param category
     * @return {@link CategoryDto}
     */
    public static CategoryDto from(Category category) {
        if (category == null) return null;
        CategoryDto dto = new CategoryDto(category.getId());
        dto.setName(category.getName());
        dto.setUser(UserMapper.from(category.getUser()));
        dto.setStatus(category.getStatus());
        if (category.getProducts() != null) {
            dto.setProducts(category.getProducts().stream().map(ProductMapper::from).toList());
        }

        return dto;
    }

    public static CategoryDto onlyId(Category category) {
        if (category == null) return null;
        CategoryDto categoryDto = new CategoryDto(category.getId());
        categoryDto.setName(category.getName());
        return categoryDto;
    }

    /**
     * {@link Category} from {@link CategoryDto}
     *
     * @param dto
     * @return {@link Category}
     */
    public static Category from(CategoryDto dto) {
        if (dto == null) return null;
        Category category = new Category(dto.getId());
        category.setName(dto.getName());
        category.setUser(UserMapper.from(dto.getUser()));
        category.setStatus(dto.getStatus());
        if (dto.getProducts() != null) {
            category.setProducts(dto.getProducts().stream().map(ProductMapper::from).toList());
        }
        return category;
    }
}
