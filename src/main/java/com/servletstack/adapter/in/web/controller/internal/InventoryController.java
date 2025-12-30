package com.servletstack.adapter.in.web.controller.internal;

import com.servletstack.adapter.in.web.annotation.Authorization;
import com.servletstack.adapter.in.web.controller.InventoryControllerApi;
import com.servletstack.adapter.in.web.controller.internal.base.BaseController;
import com.servletstack.adapter.in.web.dto.HttpResponse;
import com.servletstack.adapter.in.web.dto.IHttpResponse;
import com.servletstack.adapter.in.web.dto.IServletResponse;
import com.servletstack.application.exception.AppException;
import com.servletstack.application.mapper.InventoryMapper;
import com.servletstack.application.port.in.category.ListCategoryUseCase;
import com.servletstack.application.port.in.product.ProductDetailUserCase;
import com.servletstack.application.port.in.stock.DeleteInventoryUseCase;
import com.servletstack.application.port.in.stock.GetInventoryDetailUseCase;
import com.servletstack.application.port.in.stock.ListInventoryUseCase;
import com.servletstack.application.port.in.stock.RegisterInventoryUseCase;
import com.servletstack.application.port.in.stock.UpdateInventoryUseCase;
import com.servletstack.application.transfer.request.InventoryCreateRequest;
import com.servletstack.application.transfer.request.InventoryRequest;
import com.servletstack.application.transfer.request.ProductRequest;
import com.servletstack.application.transfer.response.CategoryResponse;
import com.servletstack.application.transfer.response.InventoryResponse;
import com.servletstack.application.transfer.response.ProductResponse;
import com.servletstack.domain.entity.Inventory;
import com.servletstack.infrastructure.persistence.transfer.IPageRequest;
import com.servletstack.infrastructure.persistence.transfer.IPageable;
import com.servletstack.shared.vo.KeyPair;
import com.servletstack.shared.vo.Query;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class InventoryController extends BaseController implements InventoryControllerApi {
    @Inject
    private InventoryMapper mapper;
    @Inject
    private ListCategoryUseCase listCategoryUseCase;
    @Inject
    private ListInventoryUseCase listInventoryUseCase;
    @Inject
    private UpdateInventoryUseCase updateInventoryUseCase;
    @Inject
    private RegisterInventoryUseCase registerInventoryUseCase;
    @Inject
    private DeleteInventoryUseCase deleteInventoryUseCase;
    @Inject
    private GetInventoryDetailUseCase getInventoryDetailUseCase;
    @Inject
    private ProductDetailUserCase productDetailUserCase;

    @Override
    protected Class<InventoryController> implementation() {
        return InventoryController.class;
    }

    @SneakyThrows
    public IHttpResponse<ProductResponse> forwardRegister(Query query, @Authorization String auth) {
        ProductResponse response = loadProductDetails(query, auth);
        return newHttpResponse(200, response, forwardTo("formCreateItem"));
    }

    @SneakyThrows
    public IHttpResponse<Void> create(InventoryCreateRequest request, @Authorization String auth) {
        InventoryResponse inventory = registerInventoryUseCase.register(request, auth);
        return newHttpResponse(201, redirectTo(inventory.getId()));
    }

    @SneakyThrows
    public IHttpResponse<Void> delete(InventoryRequest request, @Authorization String auth) {
        deleteInventoryUseCase.delete(request, auth);
        return HttpResponse.<Void>next(redirectToCtx("list")).build();
    }

    @SneakyThrows
    public IServletResponse list(IPageRequest pageRequest, InventoryRequest request, @Authorization String auth) {
        Inventory inventory = mapper.toInventory(request);
        pageRequest.setFilter(inventory);
        IServletResponse response = getServletResponse(pageRequest, auth);
        log.debug("List completed for inventory: {}", request);
        return response;
    }

    @SneakyThrows
    public IServletResponse search(Query query, IPageRequest pageRequest, @Authorization String auth) {
        Inventory inventory = mapper.toInventory(mapper.queryToInventory(query));
        pageRequest.setFilter(inventory);
        IServletResponse response = getServletResponse(pageRequest, auth);
        log.debug("Search completed with query: {}", query);
        return response;
    }

    @SneakyThrows
    public IHttpResponse<InventoryResponse> findById(InventoryRequest request, @Authorization String auth) {
        InventoryResponse inventory = getInventoryDetailUseCase.get(request, auth);
        return okHttpResponse(inventory, forwardTo("formListItem"));
    }

    @SneakyThrows
    public IHttpResponse<InventoryResponse> details(InventoryRequest request, @Authorization String auth) {
        InventoryResponse inventory = getInventoryDetailUseCase.get(request, auth);
        return okHttpResponse(inventory, forwardTo("formUpdateItem"));
    }

    @SneakyThrows
    public IHttpResponse<Void> update(InventoryRequest request, @Authorization String auth) {
        InventoryResponse inventory = updateInventoryUseCase.update(request, auth);
        return newHttpResponse(204, redirectTo(inventory.getId()));
    }

    private IServletResponse getServletResponse(IPageRequest pageRequest, String auth) throws AppException {
        IPageable<InventoryResponse> page = listInventoryUseCase.getAllPageable(
                pageRequest, auth, mapper::toResponse);
        Collection<CategoryResponse> categories = listCategoryUseCase.list(null, auth);

        Set<KeyPair> container = Set.of(
                new KeyPair("pageable", page),
                new KeyPair("categories", categories)
        );
        return newServletResponse(container, forwardTo("listItems"));
    }

    private ProductResponse loadProductDetails(Query query, String auth) throws AppException {
        if (query == null) return null;

        String productId = query.get("productId");
        if (productId == null || productId.isBlank()) return null;

        ProductRequest request = ProductRequest.builder()
                .id(UUID.fromString(productId))
                .build();
        return productDetailUserCase.get(request, auth);
    }
}
