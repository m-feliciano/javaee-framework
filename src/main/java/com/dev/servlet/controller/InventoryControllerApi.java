package com.dev.servlet.controller;

import com.dev.servlet.core.annotation.Authorization;
import com.dev.servlet.core.annotation.Controller;
import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.core.response.IServletResponse;
import com.dev.servlet.domain.records.Query;
import com.dev.servlet.domain.request.InventoryCreateRequest;
import com.dev.servlet.domain.request.InventoryRequest;
import com.dev.servlet.domain.response.InventoryResponse;
import com.dev.servlet.domain.response.ProductResponse;

import static com.dev.servlet.domain.model.enums.RequestMethod.POST;

@Controller("inventory")
public interface InventoryControllerApi {

    @RequestMapping(value = "/new")
    IHttpResponse<ProductResponse> forwardRegister(Query query, @Authorization String auth);

    @RequestMapping(value = "/create", method = POST, jsonType = InventoryCreateRequest.class)
    IHttpResponse<Void> create(InventoryCreateRequest request, @Authorization String auth);

    @RequestMapping(value = "/list", jsonType = InventoryRequest.class)
    IServletResponse list(InventoryRequest request, @Authorization String auth);

    @RequestMapping(value = "/search")
    IServletResponse search(Query query, @Authorization String auth);

    @RequestMapping(value = "/delete/{id}", method = POST, jsonType = InventoryRequest.class)
    IHttpResponse<Void> delete(InventoryRequest request, @Authorization String auth);

    @RequestMapping(value = "/list/{id}", jsonType = InventoryRequest.class)
    IHttpResponse<InventoryResponse> findById(InventoryRequest request, @Authorization String auth);

    @RequestMapping(value = "/details/{id}", jsonType = InventoryRequest.class)
    IHttpResponse<InventoryResponse> details(InventoryRequest request, @Authorization String auth);

    @RequestMapping(value = "/update/{id}", method = POST, jsonType = InventoryRequest.class)
    IHttpResponse<Void> update(InventoryRequest request, @Authorization String auth);
}

