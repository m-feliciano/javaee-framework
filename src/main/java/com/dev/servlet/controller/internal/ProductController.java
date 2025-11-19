package com.dev.servlet.controller.internal;

import com.dev.servlet.controller.ProductControllerApi;
import com.dev.servlet.controller.base.BaseController;
import com.dev.servlet.core.annotation.Authorization;
import com.dev.servlet.core.annotation.Property;
import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.mapper.ProductMapper;
import com.dev.servlet.core.response.HttpResponse;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.core.response.IServletResponse;
import com.dev.servlet.domain.model.Product;
import com.dev.servlet.domain.records.KeyPair;
import com.dev.servlet.domain.records.Query;
import com.dev.servlet.domain.request.ProductRequest;
import com.dev.servlet.domain.response.CategoryResponse;
import com.dev.servlet.domain.response.ProductResponse;
import com.dev.servlet.infrastructure.persistence.IPageRequest;
import com.dev.servlet.infrastructure.persistence.IPageable;
import com.dev.servlet.service.ICategoryService;
import com.dev.servlet.service.IProductService;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@NoArgsConstructor
@Slf4j
@Singleton
public class ProductController extends BaseController implements ProductControllerApi {

    @Inject
    private IProductService productService;
    @Inject
    private ICategoryService categoryService;
    @Inject
    private ProductMapper productMapper;

    @SneakyThrows
    public IHttpResponse<Void> register(ProductRequest request, @Authorization String auth) {
        ProductResponse product = productService.register(request, auth);
        return newHttpResponse(201, redirectTo(product.getId()));
    }

    @SneakyThrows
    public IHttpResponse<Collection<CategoryResponse>> forward(@Authorization String auth) {
        var categories = categoryService.list(null, auth);
        return newHttpResponse(302, categories, forwardTo("formCreateProduct"));
    }

    @SneakyThrows
    public IServletResponse edit(ProductRequest request, @Authorization String auth) {
        ProductResponse response = this.getProductDetail(request, auth).body();

        Collection<CategoryResponse> categories = categoryService.list(null, auth);
        Set<KeyPair> body = Set.of(
                new KeyPair("product", response),
                new KeyPair("categories", categories)
        );
        return newServletResponse(body, forwardTo("formUpdateProduct"));
    }

    @SneakyThrows
    public IServletResponse search(Query query, IPageRequest pageRequest, @Authorization String auth) {
        Product product = productMapper.queryToProduct(query, jwts.getUser(auth));
        return getServletResponse(pageRequest, auth, product);
    }

    @SneakyThrows
    public IServletResponse list(IPageRequest pageRequest, @Authorization String auth) {
        Product product = productMapper.toProduct(null, jwts.getUserId(auth));
        return getServletResponse(pageRequest, auth, product);
    }

    @SneakyThrows
    public IHttpResponse<ProductResponse> getProductDetail(ProductRequest request, @Authorization String auth) {
        ProductResponse product = productService.getProductDetail(request, auth);
        return okHttpResponse(product, forwardTo("formListProduct"));
    }

    @SneakyThrows
    public IHttpResponse<Void> update(ProductRequest request, @Authorization String auth) {
        ProductResponse response = productService.update(request, auth);
        return newHttpResponse(204, redirectTo(response.getId()));
    }

    @SneakyThrows
    public IHttpResponse<Void> delete(ProductRequest filter, @Authorization String auth) {
        productService.delete(filter, auth);
        return HttpResponse.<Void>next(redirectToCtx(LIST)).build();
    }

    @SneakyThrows
    public IHttpResponse<Void> scrape(@Authorization String auth,
                                      @Property("env") String environment,
                                      @Property("scrape.product.url") String url) {
        Optional<List<ProductResponse>> response = productService.scrape(url, environment, auth);
        return HttpResponse.<Void>next(redirectToCtx(LIST)).build();
    }

    private IServletResponse getServletResponse(IPageRequest pageRequest, String auth, Product product) throws ServiceException {
        pageRequest.setFilter(product);
        IPageable<ProductResponse> page = productService.getAllPageable(pageRequest, auth, productMapper::toResponseWithoutCategory);
        BigDecimal price = productService.calculateTotalPriceFor(page, product);
        Collection<CategoryResponse> categories = categoryService.list(null, auth);

        Set<KeyPair> container = new HashSet<>();
        container.add(new KeyPair("pageable", page));
        container.add(new KeyPair("totalPrice", price));
        container.add(new KeyPair("categories", categories));

        return newServletResponse(container, forwardTo("listProducts"));
    }
}
