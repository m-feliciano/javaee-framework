package com.dev.servlet.business;

import com.dev.servlet.business.base.BaseRequest;
import com.dev.servlet.controllers.CategoryController;
import com.dev.servlet.dto.CategoryDto;
import com.dev.servlet.interfaces.IService;
import com.dev.servlet.interfaces.ResourcePath;
import com.dev.servlet.mapper.CategoryMapper;
import com.dev.servlet.pojo.Category;
import com.dev.servlet.pojo.enums.StatusEnum;
import com.dev.servlet.pojo.records.StandardRequest;
import com.dev.servlet.utils.CacheUtil;
import com.dev.servlet.utils.CollectionUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * Category Busines
 * <p>
 * This class is responsible for handling the category business logic.
 *
 * @see BaseRequest
 * @since 1.0
 */
@Singleton
@IService("category")
public class CategoryBusiness extends BaseRequest {
    private static final String CATEGORY = "category";
    private static final String CACHE_KEY = "categories";
    public static final String FORWARD_PAGES_CATEGORY = "forward:pages/category/";
    public static final String REDIRECT_VIEW_CATEGORY = "redirect:/view/category/";
    private static final String REDIRECT_ACTION_LIST_BY_ID = REDIRECT_VIEW_CATEGORY + "list/<id>";

    private CategoryController controller;

    public CategoryBusiness() {
        // Empty constructor
    }

    @Inject
    public void setDependencies(CategoryController controller) {
        this.controller = controller;
    }

    /**
     * Forward
     *
     * @return the next path
     */
    @ResourcePath(NEW)
    public String forwardRegister(StandardRequest request) {
        return FORWARD_PAGES_CATEGORY + "formCreateCategory.jsp";
    }

    /**
     * create category.
     *
     * @param request
     * @return the string
     */
    @ResourcePath(CREATE)
    public String registerOne(StandardRequest request) {
        Category cat = new Category();
        cat.setUser(getUser(request));
        cat.setName(getParameter(request, "name"));
        cat.setStatus(StatusEnum.ACTIVE.value);
        controller.save(cat);
        request.servletResponse().setStatus(HttpServletResponse.SC_CREATED);
        CacheUtil.clear(CACHE_KEY, request.token());
        return REDIRECT_ACTION_LIST_BY_ID.replace("<id>", cat.getId().toString());
    }

    /**
     * update category.
     *
     * @param request
     * @return the string
     */
    @ResourcePath(UPDATE)
    public String update(StandardRequest request) {
        CategoryDto categoryDto = findById(request.requestObject().resourceId(), request);
        categoryDto.setName(getParameter(request, "name"));

        Category category = CategoryMapper.from(categoryDto);
        controller.update(category);

        request.servletRequest().setAttribute(CATEGORY, categoryDto);
        request.servletResponse().setStatus(HttpServletResponse.SC_NO_CONTENT);
        CacheUtil.clear(CACHE_KEY, request.token());
        return REDIRECT_ACTION_LIST_BY_ID.replace("<id>", categoryDto.getId().toString());
    }

    /**
     * List category by id.
     *
     * @param request
     * @return the string
     */
    @ResourcePath(LIST)
    public String list(StandardRequest request) {
        CategoryDto dto = findById(request.requestObject().resourceId(), request);
        if (dto != null) {
            request.servletRequest().setAttribute(CATEGORY, dto);
            return FORWARD_PAGES_CATEGORY + "formListCategory.jsp";
        }

        List<CategoryDto> all = findAll(request);
        request.servletRequest().setAttribute("categories", all);
        return FORWARD_PAGES_CATEGORY + "listCategories.jsp";

    }

    /**
     * Edit category by id.
     *
     * @param request
     * @return the string
     */
    @ResourcePath(EDIT)
    public String edit(StandardRequest request) {
        CategoryDto dto = findById(request.requestObject().resourceId(), request);
        request.servletRequest().setAttribute(CATEGORY, dto);
        return FORWARD_PAGES_CATEGORY + "formUpdateCategory.jsp";
    }

    /**
     * delete category by id.
     *
     * @param request
     * @return the string
     */
    @ResourcePath(DELETE)
    public String delete(StandardRequest request) {
        Category cat = new Category(request.requestObject().resourceId());
        cat.setUser(getUser(request));
        controller.delete(cat);
        CacheUtil.clear(CACHE_KEY, request.token());
        request.servletResponse().setStatus(HttpServletResponse.SC_NO_CONTENT);
        return REDIRECT_VIEW_CATEGORY + "list";
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
        if (id == null) return null;

        List<CategoryDto> dtoList = findAll(request);
        if (!CollectionUtils.isNullOrEmpty(dtoList)) {
            return dtoList.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
        }

        return null;
    }
}
