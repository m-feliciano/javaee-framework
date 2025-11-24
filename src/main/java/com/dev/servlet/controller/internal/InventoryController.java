package com.dev.servlet.controller.internal;

import com.dev.servlet.controller.InventoryControllerApi;
import com.dev.servlet.controller.base.BaseController;
import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.mapper.InventoryMapper;
import com.dev.servlet.core.response.HttpResponse;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.core.response.IServletResponse;
import com.dev.servlet.domain.records.KeyPair;
import com.dev.servlet.domain.records.Query;
import com.dev.servlet.domain.request.InventoryCreateRequest;
import com.dev.servlet.domain.request.InventoryRequest;
import com.dev.servlet.domain.request.ProductRequest;
import com.dev.servlet.domain.response.CategoryResponse;
import com.dev.servlet.domain.response.InventoryResponse;
import com.dev.servlet.domain.response.ProductResponse;
import com.dev.servlet.service.ICategoryService;
import com.dev.servlet.service.IProductService;
import com.dev.servlet.service.IStockService;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.Set;

@Slf4j
@NoArgsConstructor
@Singleton
public class InventoryController extends BaseController implements InventoryControllerApi {

    @Inject
    private IStockService stockService;
    @Inject
    private ICategoryService categoryService;
    @Inject
    private InventoryMapper inventoryMapper;
    @Inject
    private IProductService productService;

    @SneakyThrows
    public IHttpResponse<ProductResponse> forwardRegister(Query query, String auth) {
        ProductResponse response = loadProductDetails(query, auth);
        return newHttpResponse(200, response, forwardTo("formCreateItem"));
    }

    @SneakyThrows
    public IHttpResponse<Void> create(InventoryCreateRequest request, String auth) {
        InventoryResponse inventory = stockService.register(request, auth);
        return newHttpResponse(201, redirectTo(inventory.getId()));
    }

    @SneakyThrows
    public IHttpResponse<Void> delete(InventoryRequest request, String auth) {
        stockService.delete(request, auth);
        return HttpResponse.<Void>next(redirectToCtx("list")).build();
    }

    @SneakyThrows
    public IServletResponse list(InventoryRequest request, String auth) {
        return getServletResponse(request, auth);
    }

    @SneakyThrows
    public IServletResponse search(Query query, String auth) {
        InventoryRequest request = inventoryMapper.queryToInventory(query);
        return getServletResponse(request, auth);
    }

    @SneakyThrows
    public IHttpResponse<InventoryResponse> getStockDetail(InventoryRequest request, String auth) {
        InventoryResponse inventory = stockService.getStockDetail(request, auth);
        return okHttpResponse(inventory, forwardTo("formListItem"));
    }

    @SneakyThrows
    public IHttpResponse<InventoryResponse> edit(InventoryRequest request, String auth) {
        InventoryResponse inventory = stockService.getStockDetail(request, auth);
        return okHttpResponse(inventory, forwardTo("formUpdateItem"));
    }

    @SneakyThrows
    public IHttpResponse<Void> update(InventoryRequest request, String auth) {
        InventoryResponse inventory = stockService.update(request, auth);
        return newHttpResponse(204, redirectTo(inventory.getId()));
    }
    private IServletResponse getServletResponse(InventoryRequest request, String auth) throws ServiceException {
        Collection<InventoryResponse> inventories = stockService.list(request, auth);
        Collection<CategoryResponse> categories = categoryService.list(null, auth);
        Set<KeyPair> data = Set.of(
                new KeyPair("items", inventories),
                new KeyPair("categories", categories)
        );
        return newServletResponse(data, forwardTo("listItems"));
    }

    private ProductResponse loadProductDetails(Query query, String auth) throws ServiceException {
        if (query == null || !query.has("productId")) return null;
        String productId = query.get("productId");
        ProductRequest request = ProductRequest.builder().id(productId).build();
        return productService.getProductDetail(request, auth);
    }
}
