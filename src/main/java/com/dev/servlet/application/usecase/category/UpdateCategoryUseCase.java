package com.dev.servlet.application.usecase.category;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.port.in.category.GetCategoryDetailPort;
import com.dev.servlet.application.port.in.category.UpdateCategoryPort;
import com.dev.servlet.application.port.out.cache.CachePort;
import com.dev.servlet.application.port.out.category.CategoryRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.request.CategoryRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import com.dev.servlet.domain.entity.Category;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class UpdateCategoryUseCase implements UpdateCategoryPort {
    private static final String EVENT_NAME = "category:update";
    @Inject
    private CategoryRepositoryPort repositoryPort;
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private GetCategoryDetailPort categoryDetailPort;
    @Inject
    private CachePort cachePort;

    @Override
    public CategoryResponse update(CategoryRequest request, String auth) throws ApplicationException {
        log.debug("UpdateCategoryUseCase called with request: {} and auth: {}", request, auth);

        CategoryResponse response = categoryDetailPort.get(request, auth);
        response.setName(request.name().toUpperCase());
        repositoryPort.updateName(new Category(response.getId(), response.getName()));
        cachePort.clear(authenticationPort.extractUserId(auth), "categoryCacheKey");
        return response;
    }
}
