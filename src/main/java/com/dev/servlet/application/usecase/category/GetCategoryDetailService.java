package com.dev.servlet.application.usecase.category;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.exception.NotFoundException;
import com.dev.servlet.application.mapper.CategoryMapper;
import com.dev.servlet.application.port.in.category.GetCategoryDetailUseCase;
import com.dev.servlet.application.port.out.category.CategoryRepositoryPort;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import com.dev.servlet.domain.entity.Category;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@ApplicationScoped
public class GetCategoryDetailService implements GetCategoryDetailUseCase {
    @Inject
    private CategoryMapper mapper;
    @Inject
    private CategoryRepositoryPort repository;

    @Override
    public CategoryResponse get(UUID uuid) throws AppException {
        Category category = Category.builder().id(uuid).build();
        return mapper.toResponse(repository.find(category).orElseThrow(NotFoundException::new));
    }
}
