package com.dev.servlet.controller;

import com.dev.servlet.controller.base.BaseController;
import com.dev.servlet.core.annotation.Authentication;
import com.dev.servlet.core.annotation.Controller;
import com.dev.servlet.core.annotation.Property;
import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.mapper.ProductMapper;
import com.dev.servlet.core.response.HttpResponse;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.core.response.IServletResponse;
import com.dev.servlet.domain.model.Product;
import com.dev.servlet.service.ICategoryService;
import com.dev.servlet.service.IProductService;
import com.dev.servlet.domain.records.KeyPair;
import com.dev.servlet.domain.records.Query;
import com.dev.servlet.domain.request.ProductRequest;
import com.dev.servlet.domain.response.CategoryResponse;
import com.dev.servlet.domain.response.ProductResponse;
import com.dev.servlet.infrastructure.persistence.IPageRequest;
import com.dev.servlet.infrastructure.persistence.IPageable;
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

import static com.dev.servlet.domain.model.enums.RequestMethod.GET;
import static com.dev.servlet.domain.model.enums.RequestMethod.POST;

@NoArgsConstructor
@Slf4j
@Singleton
@Controller("product")
public class ProductController extends BaseController {

    @Inject
    private IProductService productService;
    @Inject
    private ICategoryService categoryService;
    @Inject
    private ProductMapper productMapper;

    @RequestMapping(value = "/create", method = POST, jsonType = ProductRequest.class)
    public IHttpResponse<Void> register(ProductRequest request, @Authentication String auth) throws ServiceException {
        ProductResponse product = productService.register(request, auth);
        return newHttpResponse(201, redirectTo(product.getId()));
    }

    @RequestMapping("/new")
    @SneakyThrows
    public IHttpResponse<Collection<CategoryResponse>> forward(@Authentication String auth) {
        var categories = categoryService.list(null, auth);
        return newHttpResponse(302, categories, forwardTo("formCreateProduct"));
    }

    @RequestMapping(value = "/edit/{id}", jsonType = ProductRequest.class)
    @SneakyThrows
    public IServletResponse edit(ProductRequest request, @Authentication String auth) {
        ProductResponse response = this.getProductDetail(request, auth).body();

        Collection<CategoryResponse> categories = categoryService.list(null, auth);
        Set<KeyPair> body = Set.of(
                new KeyPair("product", response),
                new KeyPair("categories", categories)
        );
        return newServletResponse(body, forwardTo("formUpdateProduct"));
    }

    @RequestMapping(value = "/search")
    @SneakyThrows
    public IServletResponse search(Query query, IPageRequest pageRequest, @Authentication String auth) {
        Product product = productMapper.queryToProduct(query, jwts.getUser(auth));
        return getServletResponse(pageRequest, auth, product);
    }

    @RequestMapping(value = "/list", jsonType = ProductRequest.class)
    @SneakyThrows
    public IServletResponse list(IPageRequest pageRequest, @Authentication String auth) {
        Product product = productMapper.toProduct(null, jwts.getUserId(auth));
        return getServletResponse(pageRequest, auth, product);
    }

    @RequestMapping(value = "/list/{id}", jsonType = ProductRequest.class)
    @SneakyThrows
    public IHttpResponse<ProductResponse> getProductDetail(ProductRequest request, @Authentication String auth) {
        ProductResponse product = productService.getProductDetail(request, auth);
        return okHttpResponse(product, forwardTo("formListProduct"));
    }

    @RequestMapping(value = "/update/{id}", method = POST, jsonType = ProductRequest.class)
    @SneakyThrows
    public IHttpResponse<Void> update(ProductRequest request, @Authentication String auth) {
        ProductResponse response = productService.update(request, auth);
        return newHttpResponse(204, redirectTo(response.getId()));
    }

    @RequestMapping(value = "/delete/{id}", method = POST, jsonType = ProductRequest.class)
    @SneakyThrows
    public IHttpResponse<Void> delete(ProductRequest filter, @Authentication String auth) {
        productService.delete(filter, auth);
        return HttpResponse.<Void>next(redirectToCtx(LIST)).build();
    }

    @RequestMapping(value = "/scrape", method = GET)
    @SneakyThrows
    public IHttpResponse<Void> scrape(@Authentication String auth,
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
