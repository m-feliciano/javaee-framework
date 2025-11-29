package com.dev.servlet.application.usecase.product;

import com.dev.servlet.application.mapper.ProductMapper;
import com.dev.servlet.application.port.in.product.ScrapeProductUseCasePort;
import com.dev.servlet.application.port.out.AuditPort;
import com.dev.servlet.application.port.out.AuthenticationPort;
import com.dev.servlet.application.transfer.response.ProductResponse;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.Status;
import com.dev.servlet.infrastructure.alert.AlertService;
import com.dev.servlet.infrastructure.audit.AuditPayload;
import com.dev.servlet.infrastructure.external.webscrape.WebScrapeServiceRegistry;
import com.dev.servlet.infrastructure.external.webscrape.builder.WebScrapeBuilder;
import com.dev.servlet.infrastructure.external.webscrape.transfer.ProductWebScrapeDTO;
import com.dev.servlet.infrastructure.persistence.repository.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.RequestContextController;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class ScrapeProductUseCase implements ScrapeProductUseCasePort {
    @Inject
    private ProductRepository productRepository;
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private AuditPort auditPort;
    @Inject
    private AlertService alertService;
    @Inject
    private WebScrapeServiceRegistry webScrapeServiceRegistry;
    @Inject
    private RequestContextController requestContextController;
    @Inject
    private ProductMapper productMapper;

    @Override
    public CompletableFuture<List<ProductResponse>> scrapeAsync(String url, String environment, String auth) {
        final User user = authenticationPort.extractUser(auth);

        CompletableFuture<List<ProductResponse>> future = CompletableFuture
                .supplyAsync(() -> {
                    requestContextController.activate();
                    try {
                        return scrape(url, environment, auth);
                    } finally {
                        requestContextController.deactivate();
                    }
                });

        alertService.publish(user.getId(), "info",
                "Web scraping started. You will be notified once it's completed");
        return future;
    }

    private List<ProductResponse> scrape(String url, String environment, String auth) {
        final User user = authenticationPort.extractUser(auth);

        if (!"development".equals(environment)) {
            log.warn("Web scraping is only allowed in development environment");
            alertService.publish(user.getId(), "warn",
                    "Web scraping is only allowed in development environment");
            return null;
        }

        Optional<List<ProductWebScrapeDTO>> optional;
        try {
            optional = WebScrapeBuilder.<List<ProductWebScrapeDTO>>create()
                    .withServiceType("product")
                    .withUrl(url)
                    .withRegistry(webScrapeServiceRegistry)
                    .onErrorHandler(ex -> {
                        auditPort.failure("product:scrape", auth, new AuditPayload<>(url, null));
                        alertService.publish(user.getId(), "error", "Error during web scraping. Try again later.");
                        throw new RuntimeException(ex);
                    })
                    .execute();

            if (optional.isEmpty()) {
                alertService.publish(user.getId(), "error", "No products found during web scraping.");
                return null;
            }

        } catch (Exception ignored) {
            return null;
        }

        List<ProductWebScrapeDTO> response = optional.get();
        log.debug("Scraped {} products from {}", response.size(), url);

        List<Product> products = response.stream()
                .map(dto -> {
                    Product p = productMapper.scrapeToProduct(dto);
                    p.setStatus(Status.ACTIVE.getValue());
                    p.setUrl(p.getUrl());
                    p.setRegisterDate(LocalDate.now());
                    p.setUser(user);
                    return p;
                })
                .toList();

        products = productRepository.saveAll(products);
        log.debug("Scraped saved {} products to the database", products.size());

        List<ProductResponse> responseList = products.stream()
                .map(p -> productMapper.toResponse(p))
                .toList();

        auditPort.success("product:scrape", auth,
                new AuditPayload<>(url, responseList,
                        Map.of("products_scraped", responseList.size())));

        String message = String.format("Successfully scraped %d products.", responseList.size());
        alertService.publish(user.getId(), "success", message);
        return responseList;
    }
}
