package com.dev.servlet.application.usecase.category;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.port.in.category.GetCategoryDetailUseCase;
import com.dev.servlet.application.port.in.category.UpdateCategoryUseCase;
import com.dev.servlet.application.port.out.category.CategoryRepositoryPort;
import com.dev.servlet.application.transfer.request.CategoryRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import com.dev.servlet.domain.entity.Category;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class UpdateCategoryService implements UpdateCategoryUseCase {
    @Inject
    private CategoryRepositoryPort repository;
    @Inject
    private GetCategoryDetailUseCase categoryUseCase;

    @Override
    public CategoryResponse update(CategoryRequest request, String auth) throws AppException {
        log.debug("UpdateCategoryUseCase called with request: {} and auth: {}", request, auth);

        CategoryResponse response = categoryUseCase.get(request, auth);
        response.setName(request.name().toUpperCase());
        repository.updateName(new Category(response.getId(), response.getName()));
        return CategoryResponse.builder().id(response.getId()).build();
    }
}
