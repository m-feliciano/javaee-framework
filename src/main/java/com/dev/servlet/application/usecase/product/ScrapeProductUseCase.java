package com.dev.servlet.application.usecase.product;

import com.dev.servlet.adapter.out.external.webscrape.WebScrapeServiceRegistry;
import com.dev.servlet.adapter.out.external.webscrape.builder.WebScrapeBuilder;
import com.dev.servlet.adapter.out.external.webscrape.transfer.ProductWebScrapeDTO;
import com.dev.servlet.adapter.out.image.ImageService;
import com.dev.servlet.application.mapper.ProductMapper;
import com.dev.servlet.application.port.in.product.ScrapeProductPort;
import com.dev.servlet.application.port.out.alert.AlertPort;
import com.dev.servlet.application.port.out.product.ProductRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.port.out.storage.StorageService;
import com.dev.servlet.application.transfer.response.ProductResponse;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.Status;
import com.dev.servlet.infrastructure.config.Properties;
import com.dev.servlet.infrastructure.http.FileHttpClient;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.RequestContextController;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@ApplicationScoped
public class ScrapeProductUseCase implements ScrapeProductPort {
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

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
    @Inject
    private StorageService storageService;
    @Inject
    private ImageService imageService;
    @Inject
    private FileHttpClient fileHttpClient;

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
        log.info("Scraped {} products from {}", response.size(), url);

        List<Product> products = response.stream()
                .map(dto -> {
                    Product prod = productMapper.scrapeToProduct(dto);
                    prod.setStatus(Status.ACTIVE.getValue());
                    prod.setRegisterDate(LocalDate.now());
                    prod.setOwner(user);
                    return prod;
                })
                .toList();

        log.debug("Uploading {} product images to storage", products.size());
        uploadImageToStorage(products, user);

        products = repositoryPort.saveAll(products);
        log.info("Scraped saved {} products to the database", products.size());

        List<ProductResponse> responseList = products.stream()
                .map(p -> productMapper.toResponse(p))
                .toList();

        String message = String.format("Successfully scraped %d products.", responseList.size());
        alertPort.publish(user.getId(), "success", message);
        return responseList;
    }

    @PreDestroy
    public void onDestroy() {
        executor.shutdown();
    }

    private void uploadImageToStorage(List<Product> products, User user) {
        List<Future<Void>> tasks = new ArrayList<>();

        for (Product prod : products) {
            if (prod.getThumbUrl() == null) continue;

            Future<Void> future = executor.submit(() -> {
                prod.setThumbUrl(uploadImage(prod.getThumbUrl(), user));
                return null;
            });

            tasks.add(future);
        }

        for (Future<Void> future : tasks) {
            try {
                future.get();
            } catch (Exception e) {
                log.warn("Error occurred while uploading product image", e);
            }
        }
    }

    private String uploadImage(String sourceUrl, User user) {
        log.info("Processing image for product from URL: {}", sourceUrl);

        try (InputStream download = fileHttpClient.download(sourceUrl);
             InputStream pic = imageService.processToSquareJpg(download, 500)) {

            final String path = "private/users/" + user.getId() + "/products/" + UUID.randomUUID() + ".jpg";
            log.info("Generating image URL for product image at path: {}", path);

            URI uri = storageService.generateUploadUri(path, "image/jpeg", Duration.ofMinutes(5));
            fileHttpClient.upload(uri, pic, "image/jpeg");
            return path;

        } catch (Exception e) {
            log.error("Failed to upload product image from URL: {}", sourceUrl, e);
            return null;
        }
    }
}
