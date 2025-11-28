package com.dev.servlet.service.internal;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.mapper.Mapper;
import com.dev.servlet.core.mapper.ProductMapper;
import com.dev.servlet.domain.model.Category;
import com.dev.servlet.domain.model.Inventory;
import com.dev.servlet.domain.model.Product;
import com.dev.servlet.domain.model.User;
import com.dev.servlet.domain.model.enums.Status;
import com.dev.servlet.domain.request.ProductRequest;
import com.dev.servlet.domain.response.ProductResponse;
import com.dev.servlet.infrastructure.external.webscrape.WebScrapeServiceRegistry;
import com.dev.servlet.infrastructure.external.webscrape.builder.WebScrapeBuilder;
import com.dev.servlet.infrastructure.external.webscrape.transfer.ProductWebScrapeDTO;
import com.dev.servlet.infrastructure.persistence.IPageRequest;
import com.dev.servlet.infrastructure.persistence.IPageable;
import com.dev.servlet.infrastructure.persistence.dao.ProductDAO;
import com.dev.servlet.service.AuditService;
import com.dev.servlet.service.IBusinessService;
import com.dev.servlet.service.IProductService;
import jakarta.enterprise.context.control.RequestContextController;
import jakarta.enterprise.inject.Model;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.dev.servlet.core.util.ThrowableUtils.notFound;
import static com.dev.servlet.core.util.ThrowableUtils.serviceError;

@Slf4j
@NoArgsConstructor
@Model
@Named("productService")
public class ProductServiceImpl extends BaseServiceImpl<Product, String> implements IProductService {
    @Inject
    private IBusinessService businessService;
    @Inject
    private ProductMapper productMapper;
    @Inject
    private WebScrapeServiceRegistry webScrapeServiceRegistry;
    @Inject
    private AuditService auditService;
    @Inject
    private AlertService alertService;
    @Inject
    private RequestContextController requestContextController;

    @Inject
    public ProductServiceImpl(ProductDAO dao) {
        super(dao);
    }

    public ProductDAO getDAO() {
        return (ProductDAO) super.getBaseDAO();
    }

    @Override
    public <U> IPageable<U> getAllPageable(IPageRequest payload, String auth, Mapper<Product, U> mapper) {
        StopWatch sw = new StopWatch();

        try {
            sw.start();
            IPageable<U> pageable = super.getAllPageable(payload, mapper);
            sw.stop();
            auditService.auditSuccess("product:list",
                    auth,
                    new AuditPayload<>(payload,
                            pageable.getContent(),
                            Map.of(
                                    "total_products", pageable.getTotalElements(),
                                    "current_page", pageable.getCurrentPage(),
                                    "page_size", pageable.getPageSize(),
                                    "time_to_complete in ms", sw.getTime(TimeUnit.MILLISECONDS))
                    ));
            return pageable;

        } catch (Exception e) {
            sw.stop();
            auditService.auditFailure("product:list", null,
                    new AuditPayload<>(payload,
                            null,
                            Map.of("time_to_fail in ms", sw.getTime(TimeUnit.MILLISECONDS))
                    ));

            throw e;
        }
    }

    @Override
    public ProductResponse register(ProductRequest request, String auth) {
        try {
            Product product = productMapper.toProduct(request, jwts.getUserId(auth));
            product.setRegisterDate(LocalDate.now());
            product.setStatus(Status.ACTIVE.getValue());
            product = super.save(product);
            ProductResponse response = productMapper.toResponse(product);
            auditService.auditSuccess("product:create", auth, new AuditPayload<>(request, response));
            return response;
        } catch (Exception e) {
            auditService.auditFailure("product:create", auth, new AuditPayload<>(request, null));
            throw e;
        }
    }

    @Override
    public ProductResponse findById(ProductRequest request, String auth) throws ServiceException {
        try {
            Product product = productMapper.toProduct(request, jwts.getUserId(auth));
            product = findProduct(product);
            ProductResponse response = productMapper.toResponse(product);
            auditService.auditSuccess("product:find_by_id", auth, new AuditPayload<>(request, response));
            return response;

        } catch (Exception e) {
            auditService.auditFailure("product:find_by_id", auth, new AuditPayload<>(request, null));
            throw e;
        }
    }

    @Override
    public ProductResponse update(ProductRequest request, String auth) throws ServiceException {
        try {
            Product product = productMapper.toProduct(request, jwts.getUserId(auth));
            product = findProduct(product);
            product.setName(request.name());
            product.setDescription(request.description());
            product.setPrice(request.price());
            product.setUrl(request.url());
            product.setCategory(Category.builder().id(request.category().id()).build());

            super.update(product);
            ProductResponse response = productMapper.toResponse(product);
            auditService.auditSuccess("product:update", auth, new AuditPayload<>(request, response));
            return response;
        } catch (Exception e) {
            auditService.auditFailure("product:update", auth, new AuditPayload<>(request, null));
            throw e;
        }
    }

    @Override
    public void delete(ProductRequest request, String auth) throws ServiceException {
        try {
            Product product = productMapper.toProduct(request, jwts.getUserId(auth));
            product = findProduct(product);

            Inventory inventory = Inventory.builder().user(product.getUser()).product(product).build();
            if (businessService.hasInventory(inventory, auth)) {
                throw serviceError(HttpServletResponse.SC_CONFLICT, "Product has inventory");
            }

            super.delete(product);
            auditService.auditSuccess("product:delete", auth, new AuditPayload<>(request, null));
        } catch (Exception e) {
            auditService.auditFailure("product:delete", auth, new AuditPayload<>(request, null));
            throw e;
        }
    }

    @Override
    public BigDecimal calculateTotalPriceFor(IPageable<?> page, Product product) {
        if (page != null && page.getContent().iterator().hasNext()) {
            ProductDAO dao = this.getDAO();
            return dao.calculateTotalPriceFor(product);
        }
        return BigDecimal.ZERO;
    }

    @SneakyThrows
    public Optional<List<ProductResponse>> scrape(String url, String environment, String auth) {
        if (!"development".equals(environment)) {
            log.warn("Web scraping is only allowed in development environment");
            auditService.auditWarning("product:scrape", auth, new AuditPayload<>(url, null));
            return Optional.empty();
        }

        final User user = jwts.getUser(auth);

        Optional<List<ProductWebScrapeDTO>> scrapeResponse = WebScrapeBuilder.<List<ProductWebScrapeDTO>>create()
                .withServiceType("product")
                .withUrl(url)
                .withRegistry(webScrapeServiceRegistry)
                .execute();
        if (scrapeResponse.isEmpty()) {
            log.warn("No products found in the web scrape response");
            auditService.auditWarning("product:scrape", auth, new AuditPayload<>(url, null));
            return Optional.empty();
        }

        List<ProductWebScrapeDTO> response = scrapeResponse.get();
        log.info("Web scrape returned {} products", response.size());

        List<Product> products = response.stream()
                .map(productMapper::scrapeToProduct)
                .map(product -> prepareProductToSave(product, user))
                .toList();
        try {
            products = baseDAO.save(products);
            List<ProductResponse> productResponses = products.stream().map(productMapper::toResponse).toList();

            auditService.auditSuccess("product:scrape", auth,
                    new AuditPayload<>(url,
                            productResponses,
                            Map.of("products_scraped", productResponses.size()
                            )));

            return Optional.of(productResponses);

        } catch (Exception e) {
            log.error("Error saving scraped products", e);
            auditService.auditFailure("product:scrape", auth, new AuditPayload<>(url, null));
            return Optional.empty();
        }
    }

    @Override
    public CompletableFuture<List<ProductResponse>> scrapeAsync(String url, String environment, String auth) {
        final String userId = jwts.getUserId(auth);

        CompletableFuture<List<ProductResponse>> future = CompletableFuture.supplyAsync(() -> {
            try {
                // Enable the context for the async operation
                requestContextController.activate();

                Optional<List<ProductResponse>> response = scrape(url, environment, auth);
                if (response.isEmpty()) {
                    alertService.publish(userId, "info", "No products were scraped from the provided URL.");
                    auditService.auditWarning("product:scrape_async", auth, new AuditPayload<>(url, null));
                    return null;
                }

                alertService.publish(userId, "success", "Successfully scraped products. Check the product list.");
                auditService.auditSuccess("product:scrape_async", auth, new AuditPayload<>(url, response.get()));
                return response.get();

            } catch (Exception e) {
                log.error("Async web scraping failed for URL: {}", url, e);
                alertService.publish(userId, "error", e.getMessage());
                auditService.auditFailure("product:scrape_async", auth, new AuditPayload<>(url, null));

            } finally {
                // Ensure the request context is deactivated after the async operation
                requestContextController.deactivate();
            }

            return null;
        });

        alertService.publish(userId, "info", "Web scraping started. You will be notified once it's completed");
        return future;
    }

    private static Product prepareProductToSave(Product product, User user) {
        product.setRegisterDate(LocalDate.now());
        product.setStatus(Status.ACTIVE.getValue());
        product.setUser(user);
        return product;
    }

    private Product findProduct(Product product) throws ServiceException {
        return this.find(product).orElseThrow(() -> notFound("Product not found"));
    }
}
