package com.dev.servlet.web.controller;

import com.dev.servlet.application.transfer.request.ProductRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import com.dev.servlet.application.transfer.response.ProductResponse;
import com.dev.servlet.domain.valueobject.Query;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.web.annotation.Authorization;
import com.dev.servlet.web.annotation.Controller;
import com.dev.servlet.web.annotation.Property;
import com.dev.servlet.web.annotation.RequestMapping;
import com.dev.servlet.web.response.IHttpResponse;
import com.dev.servlet.web.response.IServletResponse;

import java.util.Collection;

import static com.dev.servlet.domain.entity.enums.RequestMethod.POST;

@Controller("product")
public interface ProductControllerApi {
    @RequestMapping(value = "/create", method = POST, jsonType = ProductRequest.class)
    IHttpResponse<Void> register(ProductRequest request, @Authorization String auth);

    @RequestMapping("/new")
    IHttpResponse<Collection<CategoryResponse>> forward(@Authorization String auth);

    @RequestMapping(value = "/details/{id}", jsonType = ProductRequest.class)
    IServletResponse details(ProductRequest request, @Authorization String auth);

    @RequestMapping(value = "/search")
    IServletResponse search(Query query, IPageRequest pageRequest, @Authorization String auth);

    @RequestMapping(value = "/list", jsonType = ProductRequest.class)
    IServletResponse list(IPageRequest pageRequest, @Authorization String auth);

    @RequestMapping(value = "/list/{id}", jsonType = ProductRequest.class)
    IHttpResponse<ProductResponse> findById(ProductRequest request, @Authorization String auth);

    @RequestMapping(value = "/update/{id}", method = POST, jsonType = ProductRequest.class)
    IHttpResponse<Void> update(ProductRequest request, @Authorization String auth);

    @RequestMapping(value = "/delete/{id}", method = POST, jsonType = ProductRequest.class)
    IHttpResponse<Void> delete(ProductRequest filter, @Authorization String auth);

    @RequestMapping(value = "/scrape", method = POST)
    IHttpResponse<Void> scrape(@Authorization String auth,
                               @Property("app.env") String environment,
                               @Property("scrape_product_url") String url);
}
