package com.dev.servlet.business;

import com.dev.servlet.controllers.CategoryController;
import com.dev.servlet.domain.Category;
import com.dev.servlet.domain.enums.StatusEnum;
import com.dev.servlet.dto.CategoryDto;
import com.dev.servlet.filter.StandardRequest;
import com.dev.servlet.interfaces.ResourcePath;
import com.dev.servlet.mapper.CategoryMapper;
import com.dev.servlet.utils.CacheUtil;
import com.dev.servlet.utils.CollectionUtils;
import com.dev.servlet.business.base.BaseRequest;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton

/**
 * Category Busines
 * <p>
 * This class is responsible for handling the category business logic.
 *
 * @see BaseRequest
 * @since 1.0
 */
public class CategoryBusiness extends BaseRequest {

    private static final String FORWARD_PAGE_CREATE = "forward:pages/category/formCreateCategory.jsp";
    private static final String FORWARD_PAGE_LIST = "forward:pages/category/listCategories.jsp";
    private static final String FORWARD_PAGE_LIST_BY_ID = "forward:pages/category/formListCategory.jsp";
    private static final String FORWARD_PAGE_UPDATE = "forward:pages/category/formUpdateCategory.jsp";

    private static final String REDIRECT_ACTION_LIST_ALL = "redirect:category?action=list";
    private static final String REDIRECT_ACTION_LIST_BY_ID = "redirect:category?action=list&id=";

    private static final String CATEGORY = "category";
    private static final String CACHE_KEY = "categories";

    @Inject
    private CategoryController controller;

    public CategoryBusiness() {
    }

    public CategoryBusiness(CategoryController controller) {
        this.controller = controller;
    }

    /**
     * Forward
     *
     * @return the next path
     */
    @ResourcePath(value = NEW)
    public String forwardRegister(StandardRequest request) {
        return FORWARD_PAGE_CREATE;
    }

    /**
     * create category.
     *
     * @param request
     * @return the string
     */
    @ResourcePath(value = CREATE)
    public String registerOne(StandardRequest request) {
        Category cat = new Category();
        cat.setUser(getUser(request));
        cat.setName(getParameter(request, "name"));
        cat.setStatus(StatusEnum.ACTIVE.getName());
        controller.save(cat);
        return REDIRECT_ACTION_LIST_BY_ID + cat.getId();
    }

    /**
     * update category.
     *
     * @param request
     * @return the string
     */
    @ResourcePath(value = UPDATE)
    public String update(StandardRequest request) {
        Long id = Long.parseLong(getParameter(request, "id"));
        var category = controller.findById(id);
        category.setName(getParameter(request, "name"));
        category = controller.update(category);
        request.servletRequest().setAttribute(CATEGORY, category);
        return REDIRECT_ACTION_LIST_BY_ID + category.getId();
    }

    /**
     * List category by id.
     *
     * @param request
     * @return the string
     */
    @ResourcePath(value = LIST)
    public String list(StandardRequest request) {
        String id = getParameter(request, "id");
        if (id != null) {
            CategoryDto dto = findById(Long.valueOf(id), request);
            request.servletRequest().setAttribute(CATEGORY, dto);
            return FORWARD_PAGE_LIST_BY_ID;
        }

        List<CategoryDto> all = findAll(request);
        request.servletRequest().setAttribute("categories", all);
        return FORWARD_PAGE_LIST;

    }

    /**
     * Edit category by id.
     *
     * @param request
     * @return the string
     */
    @ResourcePath(value = EDIT)
    public String edit(StandardRequest request) {
        Long id = Long.valueOf(getParameter(request, "id"));
        request.servletRequest().setAttribute(CATEGORY, controller.findById(id));
        return FORWARD_PAGE_UPDATE;
    }

    /**
     * delete category by id.
     *
     * @param request
     * @return the string
     */
    @ResourcePath(value = DELETE)
    public String delete(StandardRequest request) {
        Long id = Long.valueOf(getParameter(request, "id"));
        Category cat = new Category(id);
        cat.setUser(getUser(request));
        controller.delete(cat);
        CacheUtil.clear(CACHE_KEY, request.token());
        return REDIRECT_ACTION_LIST_ALL;
    }

    /**
     * Find all
     *
     * @param request
     * @return {@link List}
     */
    public List<CategoryDto> findAll(StandardRequest request) {
        List<CategoryDto> dtoList = CacheUtil.get(CACHE_KEY, request.token());
        if (CollectionUtils.isNullOrEmpty(dtoList)) {
            Category category = new Category();
            category.setName(getParameter(request, "name"));
            category.setUser(getUser(request));
            var categories = controller.findAll(category);
            if (!CollectionUtils.isNullOrEmpty(categories)) {
                dtoList = categories.stream().map(CategoryMapper::from).toList();
                CacheUtil.set(CACHE_KEY, request.token(), dtoList);
            }
        }
        return dtoList;
    }

    /**
     * Find by ID
     *
     * @param id
     * @param request
     * @return {@link Category}
     */
    public CategoryDto findById(Long id, StandardRequest request) {
        List<CategoryDto> dtoList = findAll(request);
        if (!CollectionUtils.isNullOrEmpty(dtoList)) {
            CategoryDto categoryDto = dtoList.stream()
                    .filter(c -> c.getId().equals(id))
                    .findFirst()
                    .orElse(null);
            return categoryDto;
        }

        return null;
    }
}
