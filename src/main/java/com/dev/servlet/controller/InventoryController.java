package com.dev.servlet.controller;

import com.dev.servlet.controller.base.BaseController;
import com.dev.servlet.core.annotation.Authentication;
import com.dev.servlet.core.annotation.Controller;
import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.mapper.InventoryMapper;
import com.dev.servlet.domain.model.enums.RequestMethod;
import com.dev.servlet.domain.service.ICategoryService;
import com.dev.servlet.domain.service.IStockService;
import com.dev.servlet.domain.transfer.records.Query;
import com.dev.servlet.domain.transfer.response.CategoryResponse;
import com.dev.servlet.domain.transfer.response.InventoryResponse;
import com.dev.servlet.domain.transfer.records.KeyPair;
import com.dev.servlet.domain.transfer.request.InventoryCreateRequest;
import com.dev.servlet.domain.transfer.request.InventoryRequest;
import com.dev.servlet.core.response.HttpResponse;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.core.response.IServletResponse;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.Set;

@NoArgsConstructor
@Singleton
@Controller("inventory")
public class InventoryController extends BaseController {

    @Inject
    private IStockService stockService;
    @Inject
    private ICategoryService categoryService;
    @Inject
    private InventoryMapper inventoryMapper;

    @RequestMapping(value = "/new")
    public IHttpResponse<Void> forwardRegister() {
        return HttpResponse.<Void>next(forwardTo("formCreateItem")).build();
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST, jsonType = InventoryCreateRequest.class)
    @SneakyThrows
    public IHttpResponse<Void> create(InventoryCreateRequest request, @Authentication String auth) {
        InventoryResponse inventory = stockService.create(request, auth);
        return newHttpResponse(201, redirectTo(inventory.getId()));
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST, jsonType = InventoryRequest.class)
    @SneakyThrows
    public IHttpResponse<Void> delete(InventoryRequest request, @Authentication String auth) {
        stockService.delete(request, auth);
        return HttpResponse.<Void>next(redirectToCtx("list")).build();
    }

    @RequestMapping(value = "/list", jsonType = InventoryRequest.class)
    @SneakyThrows
    public IServletResponse list(InventoryRequest request, @Authentication String auth) {
        return getServletResponse(request, auth);
    }

    @RequestMapping(value = "/search")
    @SneakyThrows
    public IServletResponse list(Query query, @Authentication String auth) {
        InventoryRequest request = inventoryMapper.queryToInventory(query);
        return getServletResponse(request, auth);
    }

    @RequestMapping(value = "/list/{id}", jsonType = InventoryRequest.class)
    @SneakyThrows
    public IHttpResponse<InventoryResponse> findById(InventoryRequest request, @Authentication String auth) {
        InventoryResponse inventory = stockService.findById(request, auth);
        return okHttpResponse(inventory, forwardTo("formListItem"));
    }

    @RequestMapping(value = "/edit/{id}", jsonType = InventoryRequest.class)
    @SneakyThrows
    public IHttpResponse<InventoryResponse> edit(InventoryRequest request, @Authentication String auth) {
        InventoryResponse inventory = stockService.findById(request, auth);
        return okHttpResponse(inventory, forwardTo("formUpdateItem"));
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST, jsonType = InventoryRequest.class)
    @SneakyThrows
    public IHttpResponse<Void> update(InventoryRequest request, @Authentication String auth) {
        InventoryResponse inventory = stockService.update(request, auth);
        return newHttpResponse(204, redirectTo(inventory.getId()));
    }

    /**
     * Generates an IServletResponse object based on inventory and category data retrieved
     * from respective services and forwards it to a specified target page.
     *
     * @param request the inventory request containing details required to fetch inventory data
     * @param auth the authorization token to authenticate service requests
     * @return an IServletResponse containing a structured set of key-value pairs for inventories and categories
     * @throws ServiceException if an error occurs while fetching data from the stock or category service
     */
    private IServletResponse getServletResponse(InventoryRequest request, String auth) throws ServiceException {
        Collection<InventoryResponse> inventories = stockService.list(request, auth);
        Collection<CategoryResponse> categories = categoryService.list(null, auth);
        Set<KeyPair> data = Set.of(
                new KeyPair("items", inventories),
                new KeyPair("categories", categories)
        );
        return newServletResponse(data, forwardTo("listItems"));
    }
}
