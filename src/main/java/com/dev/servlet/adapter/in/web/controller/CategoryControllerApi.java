package com.dev.servlet.adapter.in.web.controller;

import com.dev.servlet.adapter.in.web.annotation.Controller;
import com.dev.servlet.adapter.in.web.annotation.RequestMapping;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.application.transfer.request.CategoryRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;

import java.util.Collection;

import static com.dev.servlet.domain.entity.enums.RequestMethod.POST;

@Controller("category")
public interface CategoryControllerApi {
    @RequestMapping(value = "/new", description = "Forward to the category registration page.")
    IHttpResponse<Void> forwardRegister();

    @RequestMapping(
            value = "/create",
            method = POST,
            jsonType = CategoryRequest.class,
            description = "Register a new category."
    )
    IHttpResponse<Void> register(CategoryRequest request, String auth);

    @RequestMapping(
            value = "/list",
            jsonType = CategoryRequest.class,
            description = "Retrieve the list of categories."
    )
    IHttpResponse<Collection<CategoryResponse>> list(CategoryRequest category, String auth);

    @RequestMapping(
            value = "/list/{id}",
            jsonType = CategoryRequest.class,
            description = "Retrieve detailed information about a specific category."
    )
    IHttpResponse<CategoryResponse> getCategoryDetail(CategoryRequest request, String auth);

    @RequestMapping(
            value = "/delete/{id}",
            method = POST,
            jsonType = CategoryRequest.class,
            description = "Delete a category by ID."
    )
    IHttpResponse<Void> delete(CategoryRequest category, String auth);

    @RequestMapping(
            value = "/details/{id}",
            jsonType = CategoryRequest.class,
            description = "Retrieve details of a specific category by ID."
    )
    IHttpResponse<CategoryResponse> details(CategoryRequest category, String auth);

    @RequestMapping(
            value = "/update/{id}",
            method = POST,
            jsonType = CategoryRequest.class,
            description = "Update an existing category."
    )
    IHttpResponse<Void> update(CategoryRequest category, String auth);
}
