package com.dev.servlet.application.usecase.category;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.port.in.category.DeleteCategoryUseCase;
import com.dev.servlet.application.port.in.category.GetCategoryDetailUseCase;
import com.dev.servlet.application.port.out.category.CategoryRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.request.CategoryRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import com.dev.servlet.domain.entity.Category;
import com.dev.servlet.domain.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@ApplicationScoped
public class DeleteCategoryService implements DeleteCategoryUseCase {
    @Inject
    private CategoryRepositoryPort repository;
    @Inject
    private AuthenticationPort auth;
    @Inject
    private GetCategoryDetailUseCase categoryDetailPort;

    @Override
    public void delete(CategoryRequest request, String auth) throws AppException {
        log.debug("DeleteCategoryUseCase called with request: {} and auth: {}", request, auth);

        UUID userId = this.auth.extractUserId(auth);
        CategoryResponse response = categoryDetailPort.get(request, auth);
        Category category = new Category(response.getId());
        category.setUser(new User(userId));
        repository.delete(category);
    }
}
