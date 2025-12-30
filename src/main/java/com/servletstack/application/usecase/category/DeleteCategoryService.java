package com.servletstack.application.usecase.category;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.port.in.category.DeleteCategoryUseCase;
import com.servletstack.application.port.in.category.GetCategoryDetailUseCase;
import com.servletstack.application.port.out.category.CategoryRepositoryPort;
import com.servletstack.application.port.out.security.AuthenticationPort;
import com.servletstack.application.transfer.request.CategoryRequest;
import com.servletstack.application.transfer.response.CategoryResponse;
import com.servletstack.domain.entity.Category;
import com.servletstack.domain.entity.User;
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
