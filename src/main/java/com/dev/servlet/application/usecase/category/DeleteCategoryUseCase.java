package com.dev.servlet.application.usecase.category;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.port.in.category.DeleteCategoryPort;
import com.dev.servlet.application.port.in.category.GetCategoryDetailPort;
import com.dev.servlet.application.port.out.cache.CachePort;
import com.dev.servlet.application.port.out.category.CategoryRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.request.CategoryRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import com.dev.servlet.domain.entity.Category;
import com.dev.servlet.domain.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class DeleteCategoryUseCase implements DeleteCategoryPort {
    @Inject
    private CategoryRepositoryPort repositoryPort;
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private GetCategoryDetailPort categoryDetailPort;

    @Inject
    private CachePort cachePort;

    @Override
    public void delete(CategoryRequest request, String auth) throws AppException {
        log.debug("DeleteCategoryUseCase called with request: {} and auth: {}", request, auth);

        String userId = authenticationPort.extractUserId(auth);
        CategoryResponse response = categoryDetailPort.get(request, auth);
        Category category = new Category(response.getId());
        category.setUser(new User(userId));
        repositoryPort.delete(category);

        cachePort.clear("categoryCacheKey", userId);
    }
}
