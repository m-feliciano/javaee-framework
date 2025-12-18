package com.dev.servlet.adapter.in.web.controller.internal;

import com.dev.servlet.adapter.in.web.annotation.Async;
import com.dev.servlet.adapter.in.web.annotation.Authorization;
import com.dev.servlet.adapter.in.web.annotation.Property;
import com.dev.servlet.adapter.in.web.controller.ProductControllerApi;
import com.dev.servlet.adapter.in.web.controller.internal.base.BaseController;
import com.dev.servlet.adapter.in.web.dto.HttpResponse;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.adapter.in.web.dto.IServletResponse;
import com.dev.servlet.application.mapper.ProductMapper;
import com.dev.servlet.application.port.in.category.ListCategoryPort;
import com.dev.servlet.application.port.in.product.CreateProductWithThumbPort;
import com.dev.servlet.application.port.in.product.DeleteProductPort;
import com.dev.servlet.application.port.in.product.ListProductContainerPort;
import com.dev.servlet.application.port.in.product.ProductDetailPort;
import com.dev.servlet.application.port.in.product.ScrapeProductPort;
import com.dev.servlet.application.port.in.product.UpdateProductPort;
import com.dev.servlet.application.port.in.product.UpdateProductThumbPort;
import com.dev.servlet.application.transfer.request.FileUploadRequest;
import com.dev.servlet.application.transfer.request.ProductRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import com.dev.servlet.application.transfer.response.ProductResponse;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.shared.vo.KeyPair;
import com.dev.servlet.shared.vo.Query;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Set;

@Slf4j
@ApplicationScoped
public class ProductController extends BaseController implements ProductControllerApi {
    @Inject
    private ProductDetailPort productDetailPort;
    @Inject
    private DeleteProductPort deleteProductPort;
    @Inject
    private UpdateProductPort updateProductPort;
    @Inject
    private CreateProductWithThumbPort createProductWithThumbPort;
    @Inject
    private ScrapeProductPort scrapeProductPort;
    @Inject
    private ListCategoryPort listCategoryPort;
    @Inject
    private ListProductContainerPort listProductContainerPort;
    @Inject
    private ProductMapper productMapper;
    @Inject
    private UpdateProductThumbPort updateProductThumbPort;

    @Override
    protected Class<ProductController> implementation() {
        return ProductController.class;
    }

    @SneakyThrows
    public IHttpResponse<Void> register(ProductRequest request, @Authorization String auth) {
        ProductResponse product = createProductWithThumbPort.execute(request, auth);
        return newHttpResponse(201, redirectTo(product.getId()));
    }

    @SneakyThrows
    public IHttpResponse<Collection<CategoryResponse>> forward(@Authorization String auth) {
        var categories = listCategoryPort.list(null, auth);
        return newHttpResponse(302, categories, forwardTo("formCreateProduct"));
    }

    @SneakyThrows
    public IServletResponse details(ProductRequest request, @Authorization String auth) {
        ProductResponse response = this.findById(request, auth).body();
        Collection<CategoryResponse> categories = listCategoryPort.list(null, auth);
        Set<KeyPair> body = Set.of(
                new KeyPair("product", response),
                new KeyPair("categories", categories)
        );
        return newServletResponse(body, forwardTo("formUpdateProduct"));
    }

    @SneakyThrows
    public IServletResponse search(Query query, IPageRequest pageRequest, @Authorization String auth) {
        User user = authenticationPort.extractUser(auth);
        Product product = productMapper.queryToProduct(query, user);
        Set<KeyPair> container = listProductContainerPort.assembleContainerResponse(pageRequest, auth, product);
        return newServletResponse(container, forwardTo("listProducts"));
    }

    @SneakyThrows
    public IServletResponse list(IPageRequest pageRequest, @Authorization String auth) {
        Product product = productMapper.toProduct(null, authenticationPort.extractUserId(auth));
        Set<KeyPair> container = listProductContainerPort.assembleContainerResponse(pageRequest, auth, product);
        return newServletResponse(container, forwardTo("listProducts"));
    }

    @SneakyThrows
    public IHttpResponse<ProductResponse> findById(ProductRequest request, @Authorization String auth) {
        ProductResponse product = productDetailPort.get(request, auth);
        return okHttpResponse(product, forwardTo("formListProduct"));
    }

    @SneakyThrows
    public IHttpResponse<Void> update(ProductRequest request, @Authorization String auth) {
        ProductResponse response = updateProductPort.update(request, auth);
        return newHttpResponse(204, redirectTo(response.getId()));
    }

    @SneakyThrows
    public IHttpResponse<Void> delete(ProductRequest filter, @Authorization String auth) {
        deleteProductPort.delete(filter, auth);
        return HttpResponse.<Void>next(redirectToCtx(LIST)).build();
    }

    @SneakyThrows
    @Async
    public IHttpResponse<Void> scrape(@Authorization String auth,
                                      @Property("scrape_product_url") String url) {
        scrapeProductPort.scrape(url, auth);
        return HttpResponse.<Void>next(redirectToCtx(LIST)).build();
    }

    @Override
    public IHttpResponse<Void> upload(FileUploadRequest request, @Authorization String auth) {
        updateProductThumbPort.updateThumb(request, auth);
        return newHttpResponse(204, redirectTo(request.id()));
    }
}
