package com.dev.servlet.service.internal;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.mapper.CategoryMapper;
import com.dev.servlet.core.util.CacheUtils;
import com.dev.servlet.core.util.CollectionUtils;
import com.dev.servlet.domain.model.Category;
import com.dev.servlet.domain.model.User;
import com.dev.servlet.domain.model.enums.Status;
import com.dev.servlet.domain.request.CategoryRequest;
import com.dev.servlet.domain.response.CategoryResponse;
import com.dev.servlet.infrastructure.persistence.dao.CategoryDAO;
import com.dev.servlet.service.AuditService;
import com.dev.servlet.service.ICategoryService;
import jakarta.enterprise.inject.Model;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;

import static com.dev.servlet.core.util.ThrowableUtils.notFound;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@Model
public class CategoryServiceImpl extends BaseServiceImpl<Category, String> implements ICategoryService {

    private static final String CACHE_KEY = "categoryCacheKey";

    @Inject
    private CategoryMapper categoryMapper;

    @Inject
    private AuditService auditService;

    @Inject
    public CategoryServiceImpl(CategoryDAO categoryDAO) {
        super(categoryDAO);
    }

    @Override
    public CategoryResponse register(CategoryRequest request, String auth) throws ServiceException {
        try {
            User user = jwts.getUser(auth);
            Category category = categoryMapper.toCategory(request);
            category.setUser(user);
            category.setStatus(Status.ACTIVE.getValue());
            category = super.save(category);

            CacheUtils.clear(user.getId(), CACHE_KEY);
            CategoryResponse response = categoryMapper.toResponse(category);
            auditService.auditSuccess("category:register", auth, new AuditPayload<>(request, response));
            return response;
        } catch (Exception e) {
            auditService.auditFailure("category:register", auth, new AuditPayload<>(request, null));
            throw e;
        }
    }

    @Override
    public CategoryResponse update(CategoryRequest request, String auth) throws ServiceException {
        try {
            String userId = jwts.getUserId(auth);
            Category category = loadCategory(request.id(), userId);
            category.setName(request.name().toUpperCase());
            super.update(category);

            CacheUtils.clear(userId, CACHE_KEY);
            CategoryResponse response = categoryMapper.toResponse(category);
            auditService.auditSuccess("category:update", auth, new AuditPayload<>(request, response));
            return response;
        } catch (Exception e) {
            auditService.auditFailure("category:update", auth, new AuditPayload<>(request, null));
            throw e;
        }
    }

    @Override
    public CategoryResponse getCategoryDetail(CategoryRequest request, String auth) throws ServiceException {
        try {
            String userId = jwts.getUserId(auth);
            Category category = loadCategory(request.id(), userId);
            CategoryResponse response = categoryMapper.toResponse(category);
            auditService.auditSuccess("category:get_by_id", auth, new AuditPayload<>(request, response));
            return response;
        } catch (Exception e) {
            auditService.auditFailure("category:get_by_id", auth, new AuditPayload<>(request, null));
            throw e;
        }
    }

    @Override
    public Collection<CategoryResponse> list(CategoryRequest request, String token) {
        try {
            User user = jwts.getUser(token);

            Collection<CategoryResponse> categories = getAll(user);
            if (request != null && request.name() != null) {
                String lowerCase = request.name().toLowerCase();
                categories = categories.stream()
                        .filter(c -> c.getName().toLowerCase().contains(lowerCase))
                        .toList();
            }
            auditService.auditSuccess("category:list", token, new AuditPayload<>(request, categories));
            return categories;

        } catch (Exception e) {
            auditService.auditFailure("category:list", token, new AuditPayload<>(request, null));
            throw e;
        }
    }

    @Override
    public void delete(CategoryRequest request, String auth) throws ServiceException {
        try {
            String userId = jwts.getUserId(auth);
            Category category = loadCategory(request.id(), userId);
            super.delete(category);

            CacheUtils.clear(userId, CACHE_KEY);
            auditService.auditSuccess("category:delete", auth, new AuditPayload<>(request, null));
        } catch (Exception e) {
            auditService.auditFailure("category:delete", auth, new AuditPayload<>(request, null));
            throw e;
        }
    }

    private Collection<CategoryResponse> getAll(User user) {
        final String userId = user.getId();

        List<CategoryResponse> response = CacheUtils.get(userId, CACHE_KEY);
        if (CollectionUtils.isEmpty(response)) {
            var categories = super.findAll(new Category(user));
            if (!CollectionUtils.isEmpty(categories)) {
                response = categories.stream().map(categoryMapper::toResponse).toList();
                CacheUtils.set(userId, CACHE_KEY, response);
            }
        }

        return response;
    }

    private Category loadCategory(String request, String userId) throws ServiceException {
        Category category = Category.builder().id(request).user(new User(userId)).build();
        return this.find(category).orElseThrow(() -> notFound("Category not found"));
    }
}
