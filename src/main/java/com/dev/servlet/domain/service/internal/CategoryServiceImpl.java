package com.dev.servlet.domain.service.internal;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.mapper.CategoryMapper;
import com.dev.servlet.core.util.CacheUtils;
import com.dev.servlet.core.util.CollectionUtils;
import com.dev.servlet.domain.model.Category;
import com.dev.servlet.domain.model.enums.Status;
import com.dev.servlet.domain.service.ICategoryService;
import com.dev.servlet.domain.transfer.dto.CategoryDTO;
import com.dev.servlet.domain.transfer.request.Request;
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
    public static final String NAME = "name";
    private static final String CACHE_KEY = "categoryCacheKey";

    @Inject
    public CategoryServiceImpl(CategoryDAO categoryDAO) {
        super(categoryDAO);
    }

    @Override
    public Class<CategoryDTO> getDataMapper() {
        return CategoryDTO.class;
    }

    @Override
    public Category toEntity(Object object) {
        return CategoryMapper.from((CategoryDTO) object);
    }

    @Override
    public Category getBody(Request request) {
        return requestBody(request.getBody())
                .map(category -> {
                    category.setUser(getUser(request.getToken()));
                    return category;
                })
                .orElse(null);
    }

    @Override
    public CategoryDTO register(Request request) throws ServiceException {
        log.trace("");
        Category category = this.getBody(request);
        category.setStatus(Status.ACTIVE.getValue());
        category = super.save(category);
        CacheUtils.clear(CACHE_KEY, request.getToken());
        return CategoryMapper.from(category);
    }

    @Override
    public CategoryDTO update(Request request) throws ServiceException {
        log.trace("");
        Category category = require(request.id());
        category.setName(request.getParameter(NAME).toUpperCase());
        super.update(category);
        CacheUtils.clear(CACHE_KEY, request.getToken());
        return CategoryMapper.from(category);
    }

    @Override
    public CategoryDTO getById(Request request) throws ServiceException {
        log.trace("");
        Category category = require(request.id());
        return CategoryMapper.from(category);
    }

    @Override
    public Collection<CategoryDTO> list(Request request) {
        log.trace("");
        Collection<CategoryDTO> categories = getAll(request.getToken());

        if (request.getParameter(NAME) != null) {
            String lowerCase = request.getParameter(NAME).toLowerCase();
            categories = categories.stream()
                    .filter(c -> c.getName().toLowerCase().contains(lowerCase))
                    .toList();
        }
        return categories;
    }

    @Override
    public boolean delete(Request request) throws ServiceException {
        log.trace("");
        Category category = require(request.id());
        super.delete(category);
        CacheUtils.clear(CACHE_KEY, request.getToken());
        return true;
    }

    private Collection<CategoryDTO> getAll(String token) {
        List<CategoryDTO> dtoList = CacheUtils.get(CACHE_KEY, token);
        if (CollectionUtils.isEmpty(dtoList)) {
            Category category = new Category(getUser(token));
            var categories = super.findAll(category);
            if (!CollectionUtils.isEmpty(categories)) {
                dtoList = categories.stream().map(CategoryMapper::from).toList();
                CacheUtils.set(CACHE_KEY, token, dtoList);
            }
        }
        return dtoList;
    }

    private Category require(String request) throws ServiceException {
        return this.findById(request).orElseThrow(() -> notFound("Category not found"));
    }
}
