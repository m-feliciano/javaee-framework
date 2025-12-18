package com.dev.servlet.application.usecase.category;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.port.in.category.GetCategoryDetailPort;
import com.dev.servlet.application.port.in.category.UpdateCategoryPort;
import com.dev.servlet.application.port.out.category.CategoryRepositoryPort;
import com.dev.servlet.application.transfer.request.CategoryRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import com.dev.servlet.domain.entity.Category;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class UpdateCategoryUseCase implements UpdateCategoryPort {
    @Inject
    private CategoryRepositoryPort repositoryPort;
    @Inject
    private GetCategoryDetailPort categoryDetailPort;

    @Override
    public CategoryResponse update(CategoryRequest request, String auth) throws AppException {
        log.debug("UpdateCategoryUseCase called with request: {} and auth: {}", request, auth);

        CategoryResponse response = categoryDetailPort.get(request, auth);
        response.setName(request.name().toUpperCase());
        repositoryPort.updateName(new Category(response.getId(), response.getName()));

        return new CategoryResponse(response.getId());
    }
}
