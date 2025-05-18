package com.dev.servlet.controllers;

import com.dev.servlet.dto.CategoryDTO;
import com.dev.servlet.dto.ServiceException;
import com.dev.servlet.interfaces.Constraints;
import com.dev.servlet.interfaces.Controller;
import com.dev.servlet.interfaces.IHttpResponse;
import com.dev.servlet.interfaces.RequestMapping;
import com.dev.servlet.interfaces.Validator;
import com.dev.servlet.model.CategoryModel;
import com.dev.servlet.pojo.domain.Category;
import com.dev.servlet.pojo.enums.RequestMethod;
import com.dev.servlet.pojo.records.HttpResponse;
import com.dev.servlet.pojo.records.Request;
import lombok.NoArgsConstructor;

import javax.inject.Inject;
import java.util.Collection;


@NoArgsConstructor
@Controller(path = "/category")
public final class CategoryController extends BaseController<Category, Long> {

    @Inject
    public CategoryController(CategoryModel categoryModel) {
        super(categoryModel);
    }

    private CategoryModel getModel() {
        return (CategoryModel) super.getBaseModel();
    }

    /**
     * Forward to the register form.
     *
     * @return the response {@linkplain IHttpResponse} with the next path
     */
    @RequestMapping(value = "/new")
    public IHttpResponse<Void> forwardRegister() {

        return HttpResponse.ofNext(super.forwardTo("formCreateCategory"));
    }

    /**
     * Delete a category.
     *
     * @param request the request containing the category id
     * @return {@linkplain IHttpResponse} with no content {@linkplain Void}
     * @throws ServiceException if an error occurs during deletion
     */
    @RequestMapping(
            value = "/delete/{id}",
            method = RequestMethod.POST,
            validators = {
                    @Validator(values = "id", constraints = {
                            @Constraints(min = 1, message = "ID must be greater than or equal to {0}")
                    })
            })
    public IHttpResponse<Void> delete(Request request) throws ServiceException {
        CategoryModel model = this.getModel();
        model.delete(request);

        return HttpResponse.ofNext(super.redirectTo(LIST));
    }

    /**
     * Edit a category.
     *
     * @param request the request containing the category id
     * @return the response {@linkplain IHttpResponse} with the next path
     * @throws ServiceException if an error occurs during edition
     */
    @RequestMapping(
            value = "/edit/{id}",
            validators = {
                    @Validator(values = "id", constraints = {
                            @Constraints(min = 1, message = "ID must be greater than or equal to {0}")
                    })
            })
    public IHttpResponse<CategoryDTO> edit(Request request) throws ServiceException {
        CategoryModel model = this.getModel();
        CategoryDTO category = model.listById(request);
        // OK
        return super.okHttpResponse(category, super.forwardTo("formUpdateCategory"));
    }

    /**
     * Register a new category.
     *
     * @param request the request containing the category data
     * @return the response {@linkplain IHttpResponse} with the next path
     * @throws ServiceException if an error occurs during registration
     */
    @RequestMapping(
            value = "/create",
            method = RequestMethod.POST,
            validators = {
                    @Validator(values = "name", constraints = {
                            @Constraints(minLength = 3, maxLength = 50, message = "Name must be between {0} and {1} characters")
                    })
            })
    public IHttpResponse<Void> register(Request request) throws ServiceException {
        CategoryModel model = this.getModel();
        CategoryDTO category = model.register(request);
        // Created
        return super.newHttpResponse(201, null, super.redirectTo(category.getId()));
    }

    /**
     * Update a category.
     *
     * @param request the request containing the category data
     * @return the response {@linkplain IHttpResponse} with the next path
     * @throws ServiceException if an error occurs during update
     */
    @RequestMapping(
            value = "/update/{id}",
            method = RequestMethod.POST,
            validators = {
                    @Validator(values = "id", constraints = {
                            @Constraints(min = 1, message = "ID must be greater than or equal to {0}")
                    }),
                    @Validator(values = "name", constraints = {
//                            @Constraints(minLength = 5, maxLength = 50, message = "Name must be between {0} and {1} characters"),
                            @Constraints(minLength = 5, message = "Name must be at least {0} characters"),
                            @Constraints(maxLength = 50, message = "Name must be at most {0} characters")
                    })
            })
    public IHttpResponse<Void> update(Request request) throws ServiceException {
        CategoryModel model = this.getModel();
        CategoryDTO category = model.update(request);
        // No Content
        return super.newHttpResponse(204, null, super.redirectTo(category.getId()));
    }

    /**
     * List all categories.
     *
     * @param request the request containing the category data
     * @return the response {@linkplain IHttpResponse} with the next path
     */
    @RequestMapping(value = "/list")
    public IHttpResponse<Collection<CategoryDTO>> list(Request request) {
        CategoryModel model = this.getModel();
        Collection<CategoryDTO> categories = model.list(request);
        // OK
        return super.okHttpResponse(categories, super.forwardTo("listCategories"));
    }

    /**
     * List a category by id.
     *
     * @param request the request containing the category id
     * @return the response {@linkplain IHttpResponse} with the next path
     */
    @RequestMapping(
            value = "/list/{id}",
            validators = {
                    @Validator(values = "id", constraints = {
                            @Constraints(min = 1, message = "ID must be greater than or equal to {0}")
                    })
            })
    public IHttpResponse<CategoryDTO> listById(Request request) throws ServiceException {
        CategoryModel model = this.getModel();
        CategoryDTO category = model.listById(request);
        // OK
        return super.okHttpResponse(category, super.forwardTo("formListCategory"));
    }
}
