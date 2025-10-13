package com.dev.servlet.domain.service.internal;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.mapper.CategoryMapper;
import com.dev.servlet.core.util.CacheUtils;
import com.dev.servlet.core.util.CollectionUtils;
import com.dev.servlet.domain.model.Category;
import com.dev.servlet.domain.model.User;
import com.dev.servlet.domain.model.enums.Status;
import com.dev.servlet.domain.service.AuditService;
import com.dev.servlet.domain.service.ICategoryService;
import com.dev.servlet.domain.transfer.request.CategoryRequest;
import com.dev.servlet.domain.transfer.response.CategoryResponse;
import com.dev.servlet.infrastructure.persistence.dao.CategoryDAO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Model;
import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

import static com.dev.servlet.core.util.CryptoUtils.getUser;
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
        log.trace("");

        try {
            User user = getUser(auth);
            Category category = categoryMapper.toCategory(request);
            category.setUser(user);
            category.setStatus(Status.ACTIVE.getValue());
            category = super.save(category);

            CacheUtils.clear(CACHE_KEY, auth);
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
        log.trace("");

        try {
            Category category = loadUser(request.id(), getUser(auth));
            category.setName(request.name().toUpperCase());
            super.update(category);

            CacheUtils.clear(CACHE_KEY, auth);
            CategoryResponse response = categoryMapper.toResponse(category);
            auditService.auditSuccess("category:update", auth, new AuditPayload<>(request, response));
            return response;
        } catch (Exception e) {
            auditService.auditFailure("category:update", auth, new AuditPayload<>(request, null));
            throw e;
        }
    }

    @Override
    public CategoryResponse getById(CategoryRequest request, String auth) throws ServiceException {
        log.trace("");

        try {
            Category category = loadUser(request.id(), getUser(auth));
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
        log.trace("");

        try {
            Collection<CategoryResponse> categories = getAll(token);
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
        log.trace("");

        try {
            final User user = getUser(auth);
            Category category = loadUser(request.id(), user);
            super.delete(category);

            CacheUtils.clear(CACHE_KEY, auth);
            auditService.auditSuccess("category:delete", auth, new AuditPayload<>(request, null));
        } catch (Exception e) {
            auditService.auditFailure("category:delete", auth, new AuditPayload<>(request, null));
            throw e;
        }
    }

    private Collection<CategoryResponse> getAll(String token) {
        List<CategoryResponse> dtoList = CacheUtils.get(CACHE_KEY, token);
        if (CollectionUtils.isEmpty(dtoList)) {

            Category category = Category.builder().user(getUser(token)).build();
            var categories = super.findAll(category);
            if (!CollectionUtils.isEmpty(categories)) {
                dtoList = categories.stream()
                        .map(c -> categoryMapper.toResponse(c))
                        .toList();
                CacheUtils.set(CACHE_KEY, token, dtoList);
            }
        }
        return dtoList;
    }

    private Category loadUser(String request, User user) throws ServiceException {
        log.trace("");

        Category category = Category.builder().id(request).user(user).build();
        return this.find(category).orElseThrow(() -> notFound("Category not found"));
    }
}
