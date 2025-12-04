package com.dev.servlet.application.usecase.category;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.mapper.CategoryMapper;
import com.dev.servlet.application.port.in.category.RegisterCategoryPort;
import com.dev.servlet.application.port.out.cache.CachePort;
import com.dev.servlet.application.port.out.category.CategoryRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.request.CategoryRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import com.dev.servlet.domain.entity.Category;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.Status;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class RegisterCategoryUseCase implements RegisterCategoryPort {
    @Inject
    private CategoryMapper categoryMapper;
    @Inject
    private CategoryRepositoryPort categoryRepositoryPort;
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private CachePort cachePort;

    @Override
    public CategoryResponse register(CategoryRequest request, String auth) throws ApplicationException {
        log.debug("RegisterCategoryUseCase called with request: {} and auth: {}", request, auth);

        User user = authenticationPort.extractUser(auth);
        Category category = categoryMapper.toCategory(request);
        category.setUser(user);
        category.setStatus(Status.ACTIVE.getValue());
        category = categoryRepositoryPort.save(category);
        cachePort.clear(user.getId(), "categoryCacheKey");

        return categoryMapper.toResponse(category);
    }
}
