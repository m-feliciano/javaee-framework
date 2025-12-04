package com.dev.servlet.application.usecase.product;

import com.dev.servlet.adapter.out.external.webscrape.WebScrapeServiceRegistry;
import com.dev.servlet.adapter.out.external.webscrape.builder.WebScrapeBuilder;
import com.dev.servlet.adapter.out.external.webscrape.transfer.ProductWebScrapeDTO;
import com.dev.servlet.application.mapper.ProductMapper;
import com.dev.servlet.application.port.in.product.ScrapeProductPort;
import com.dev.servlet.application.port.out.alert.AlertPort;
import com.dev.servlet.application.port.out.product.ProductRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.response.ProductResponse;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.Status;
import com.dev.servlet.infrastructure.config.Properties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.RequestContextController;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@ApplicationScoped
public class ScrapeProductUseCase implements ScrapeProductPort {
    @Inject
    private ProductRepositoryPort repositoryPort;
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private AlertPort alertPort;
    @Inject
    private WebScrapeServiceRegistry webScrapeServiceRegistry;
    @Inject
    private RequestContextController requestContextController;
    @Inject
    private ProductMapper productMapper;

    @Override
    public CompletableFuture<List<ProductResponse>> scrapeAsync(String url, String auth) {
        final User user = authenticationPort.extractUser(auth);

        CompletableFuture<List<ProductResponse>> future = CompletableFuture
                .supplyAsync(() -> {
                    requestContextController.activate();
                    try {
                        return scrape(url, auth);
                    } finally {
                        requestContextController.deactivate();
                    }
                });

        alertPort.publish(user.getId(), "info",
                "Web scraping started. You will be notified once it's completed");
        return future;
    }

    private List<ProductResponse> scrape(String url, String auth) {
        final User user = authenticationPort.extractUser(auth);

        // Enable web scraping only in the development environment or demo mode
        if (!Properties.isDevelopmentMode() && !Properties.isDemoModeEnabled()) {
            log.warn("Web scraping is only allowed in development environment");
            alertPort.publish(user.getId(), "warn",
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
                        // do some stuff when an error occurs
                        throw new RuntimeException(ex);
                    })
                    .execute();

            if (optional.isEmpty()) {
                alertPort.publish(user.getId(), "error", "No products found during web scraping.");
                return null;
            }

        } catch (Exception ignored) {
            alertPort.publish(user.getId(), "error", "Error during web scraping. Try again later.");
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

        products = repositoryPort.saveAll(products);
        log.debug("Scraped saved {} products to the database", products.size());

        List<ProductResponse> responseList = products.stream()
                .map(p -> productMapper.toResponse(p))
                .toList();

        String message = String.format("Successfully scraped %d products.", responseList.size());
        alertPort.publish(user.getId(), "success", message);
        return responseList;
    }
}
