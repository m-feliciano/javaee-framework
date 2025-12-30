package com.servletstack.adapter.in.web.controller;

import com.servletstack.adapter.in.web.annotation.Controller;
import com.servletstack.adapter.in.web.annotation.RequestMapping;
import com.servletstack.adapter.in.web.dto.IHttpResponse;
import com.servletstack.adapter.in.web.dto.IServletResponse;
import com.servletstack.application.transfer.request.FileUploadRequest;
import com.servletstack.application.transfer.request.ProductRequest;
import com.servletstack.application.transfer.response.CategoryResponse;
import com.servletstack.application.transfer.response.ProductResponse;
import com.servletstack.infrastructure.persistence.transfer.IPageRequest;
import com.servletstack.shared.vo.Query;

import java.util.Collection;

import static com.servletstack.domain.entity.enums.RequestMethod.POST;

@Controller("product")
public interface ProductControllerApi {

    @RequestMapping(
            value = "/create",
            method = POST,
            jsonType = ProductRequest.class,
            description = "Create a new product."
    )
    IHttpResponse<Void> register(ProductRequest request, String auth);

    @RequestMapping(
            value = "/new",
            description = "Forward to the product registration page and retrieve available categories."
    )
    IHttpResponse<Collection<CategoryResponse>> forward(String auth);

    @RequestMapping(
            value = "/details/{id}",
            jsonType = ProductRequest.class,
            description = "Retrieve detailed information about a specific product by ID."
    )
    IServletResponse details(ProductRequest request, String auth);

    @RequestMapping(
            value = "/search",
            description = "Search products based on query parameters with pagination."
    )
    IServletResponse search(Query query, IPageRequest pageRequest, String auth);

    @RequestMapping(
            value = "/list",
            jsonType = ProductRequest.class,
            description = "Retrieve paginated list of products."
    )
    IServletResponse list(IPageRequest pageRequest, String auth);

    @RequestMapping(
            value = "/list/{id}",
            jsonType = ProductRequest.class,
            description = "Retrieve product information by ID."
    )
    IHttpResponse<ProductResponse> findById(ProductRequest request, String auth);

    @RequestMapping(
            value = "/update/{id}",
            method = POST,
            jsonType = ProductRequest.class,
            description = "Update an existing product."
    )
    IHttpResponse<Void> update(ProductRequest request, String auth);

    @RequestMapping(
            value = "/delete/{id}",
            method = POST,
            jsonType = ProductRequest.class,
            description = "Delete a product by ID."
    )
    IHttpResponse<Void> delete(ProductRequest filter, String auth);

    @RequestMapping(
            value = "/scrape",
            method = POST,
            description = "Scrape product information from an external URL. DEMO or TEST use only. Runs asynchronously."
    )
    IHttpResponse<Void> scrape(String auth, String url);

    @RequestMapping(
            value = "/upload-picture/{id}",
            apiVersion = "v2",
            method = POST,
            jsonType = FileUploadRequest.class,
            description = "Upload a product picture. Accepts file upload. V2 API."
    )
    IHttpResponse<Void> upload(FileUploadRequest request, String auth);
}
