package com.dev.servlet.web.controller.internal;

import com.dev.servlet.application.mapper.ProductMapper;
import com.dev.servlet.application.port.in.category.ListCategoryUseCasePort;
import com.dev.servlet.application.port.in.product.DeleteProductUseCasePort;
import com.dev.servlet.application.port.in.product.ListProductContainerUseCasePort;
import com.dev.servlet.application.port.in.product.ProductDetailUseCasePort;
import com.dev.servlet.application.port.in.product.RegisterProductUseCasePort;
import com.dev.servlet.application.port.in.product.ScrapeProductUseCasePort;
import com.dev.servlet.application.port.in.product.UpdateProductUseCasePort;
import com.dev.servlet.application.transfer.request.ProductRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import com.dev.servlet.application.transfer.response.ProductResponse;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.valueobject.KeyPair;
import com.dev.servlet.domain.valueobject.Query;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.web.controller.ProductControllerApi;
import com.dev.servlet.web.controller.internal.base.BaseController;
import com.dev.servlet.web.response.HttpResponse;
import com.dev.servlet.web.response.IHttpResponse;
import com.dev.servlet.web.response.IServletResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Set;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class ProductController extends BaseController implements ProductControllerApi {
    @Inject
    private ProductDetailUseCasePort productDetailUseCasePort;
    @Inject
    private DeleteProductUseCasePort deleteProductUseCasePort;
    @Inject
    private UpdateProductUseCasePort updateProductUseCasePort;
    @Inject
    private RegisterProductUseCasePort registerProductUseCasePort;
    @Inject
    private ScrapeProductUseCasePort scrapeProductUseCasePort;
    @Inject
    private ListCategoryUseCasePort listCategoryUseCasePort;
    @Inject
    private ListProductContainerUseCasePort listProductContainerUseCasePort;
    @Inject
    private ProductMapper productMapper;

    @SneakyThrows
    public IHttpResponse<Void> register(ProductRequest request, String auth) {
        ProductResponse product = registerProductUseCasePort.register(request, auth);
        return newHttpResponse(201, redirectTo(product.getId()));
    }

    @SneakyThrows
    public IHttpResponse<Collection<CategoryResponse>> forward(String auth) {
        var categories = listCategoryUseCasePort.list(null, auth);
        return newHttpResponse(302, categories, forwardTo("formCreateProduct"));
    }

    @SneakyThrows
    public IServletResponse details(ProductRequest request, String auth) {
        ProductResponse response = this.findById(request, auth).body();
        Collection<CategoryResponse> categories = listCategoryUseCasePort.list(null, auth);
        Set<KeyPair> body = Set.of(
                new KeyPair("product", response),
                new KeyPair("categories", categories)
        );
        return newServletResponse(body, forwardTo("formUpdateProduct"));
    }

    @SneakyThrows
    public IServletResponse search(Query query, IPageRequest pageRequest, String auth) {
        User user = authenticationPort.extractUser(auth);
        Product product = productMapper.queryToProduct(query, user);
        Set<KeyPair> container = listProductContainerUseCasePort.assembleContainerResponse(pageRequest, auth, product);
        return newServletResponse(container, forwardTo("listProducts"));
    }

    @SneakyThrows
    public IServletResponse list(IPageRequest pageRequest, String auth) {
        Product product = productMapper.toProduct(null, authenticationPort.extractUserId(auth));
        Set<KeyPair> container = listProductContainerUseCasePort.assembleContainerResponse(pageRequest, auth, product);
        return newServletResponse(container, forwardTo("listProducts"));
    }

    @SneakyThrows
    public IHttpResponse<ProductResponse> findById(ProductRequest request, String auth) {
        ProductResponse product = productDetailUseCasePort.get(request, auth);
        return okHttpResponse(product, forwardTo("formListProduct"));
    }

    @SneakyThrows
    public IHttpResponse<Void> update(ProductRequest request, String auth) {
        ProductResponse response = updateProductUseCasePort.update(request, auth);
        return newHttpResponse(204, redirectTo(response.getId()));
    }

    @SneakyThrows
    public IHttpResponse<Void> delete(ProductRequest filter, String auth) {
        deleteProductUseCasePort.delete(filter, auth);
        return HttpResponse.<Void>next(redirectToCtx(LIST)).build();
    }

    @SneakyThrows
    public IHttpResponse<Void> scrape(String auth, String environment, String url) {
        scrapeProductUseCasePort.scrapeAsync(url, environment, auth);
        return HttpResponse.<Void>next(redirectToCtx(LIST)).build();
    }
}
