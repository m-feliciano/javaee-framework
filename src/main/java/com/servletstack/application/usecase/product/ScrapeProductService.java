package com.servletstack.application.usecase.product;

import com.servletstack.adapter.out.external.webscrape.WebScrapeServiceRegistry;
import com.servletstack.adapter.out.external.webscrape.builder.WebScrapeBuilder;
import com.servletstack.adapter.out.external.webscrape.transfer.ProductWebScrapeDTO;
import com.servletstack.adapter.out.image.ImageService;
import com.servletstack.application.mapper.ProductMapper;
import com.servletstack.application.port.in.product.ScrapeProductUseCase;
import com.servletstack.application.port.out.alert.AlertPort;
import com.servletstack.application.port.out.image.FileImageRepositoryPort;
import com.servletstack.application.port.out.product.ProductRepositoryPort;
import com.servletstack.application.port.out.security.AuthenticationPort;
import com.servletstack.application.port.out.storage.StorageService;
import com.servletstack.application.transfer.response.ProductResponse;
import com.servletstack.domain.entity.FileImage;
import com.servletstack.domain.entity.Product;
import com.servletstack.domain.entity.User;
import com.servletstack.domain.entity.enums.Status;
import com.servletstack.infrastructure.config.Properties;
import com.servletstack.infrastructure.http.FileHttpClient;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@ApplicationScoped
public class ScrapeProductService implements ScrapeProductUseCase {
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    @Inject
    private ProductRepositoryPort repository;
    @Inject
    private FileImageRepositoryPort fileImageRepository;
    @Inject
    private AuthenticationPort authentication;
    @Inject
    private AlertPort alert;
    @Inject
    private WebScrapeServiceRegistry registry;
    @Inject
    private ProductMapper mapper;
    @Inject
    private StorageService storage;
    @Inject
    private ImageService imageService;
    @Inject
    private FileHttpClient fileClient;

    private static String composeFilePath(UUID userId, UUID productId, String thumbName) {
        return "private/users/" + userId + "/products/" + productId + "/thumb/" + thumbName;
    }

    private static String randomThumbName() {
        return UUID.randomUUID().toString().substring(0, 8) + ".jpg";
    }

    @PreDestroy
    public void onDestroy() {
        executor.shutdown();
    }

    public List<ProductResponse> scrape(String url, String auth) {
        final User user = authentication.extractUser(auth);

        // Enable web scraping only in the development environment or demo mode
        if (!Properties.isDevelopmentMode() && !Properties.isDemoModeEnabled()) {
            log.warn("Web scraping is only allowed in development environment");
            alert.publish(user.getId(), "warn",
                    "Web scraping is only allowed in development environment");
            return null;
        }

        var optional = WebScrapeBuilder.<List<ProductWebScrapeDTO>>create()
                .withServiceType("product")
                .withUrl(url)
                .withRegistry(registry)
                .onErrorHandler(ex ->
                        alert.publish(user.getId(), "error", "Error during web scraping. Try again later."))
                .execute();

        if (optional.isEmpty()) return null;

        List<ProductWebScrapeDTO> response = optional.get();
        log.info("Scraped {} products from {}", response.size(), url);

        List<Product> products = response.stream()
                .map(pws -> {
                    Product prod = mapper.scrapeToProduct(pws);
                    prod.setStatus(Status.ACTIVE.getValue());
                    prod.setRegisterDate(LocalDate.now());
                    prod.setThumbnails(
                            List.of(
                                    FileImage.builder()
                                            .externalSource(pws.getUrl())
                                            .product(prod)
                                            .build())
                    );
                    prod.setOwner(user);
                    return prod;
                })
                .toList();

        log.debug("Saving {} scraped products to the database", products.size());
        repository.saveAll(products);

        List<FileImage> thumbs = filterThumbsToSave(products, user);

        log.debug("Saving {} product images to the database", thumbs.size());
        fileImageRepository.saveAll(thumbs);

        log.debug("Uploading {} product images to storage", products.size());
        uploadThumbsToStorage(thumbs);

        log.debug("Finished scraping products.");

        alert.publish(user.getId(), "success",
                String.format("Successfully scraped %d products.", products.size()));

        return products.stream()
                .map(p -> new ProductResponse(p.getId()))
                .toList();
    }

    private void uploadThumbsToStorage(List<FileImage> thumbs) {
        List<Future<Void>> tasks = new ArrayList<>();

        for (var thumb : thumbs) {
            Future<Void> future = executor.submit(() -> {
                InputStream in = downloadFile(thumb.getExternalSource());
                uploadImage(in, thumb.getUri());
                in.close();

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

    private void uploadImage(InputStream in, String path) {
        log.debug("Processing file optimization...");

        try (InputStream pic = imageService.processToOptimizedJpg(in, 500, "product-thumbnail")) {
            log.debug("Generating image URL for product image at path: {}", path);
            URI uploadUri = storage.generateUploadUri(path, "image/jpeg", Duration.ofMinutes(5));
            fileClient.upload(uploadUri, pic, "image/jpeg");

        } catch (Exception e) {
            log.error("Failed to upload product image from URL: {}", path, e);
        }
    }

    private InputStream downloadFile(String url) {
        log.debug("Downloading image...");

        try (InputStream in = fileClient.download(url)) {
            return new ByteArrayInputStream(in.readAllBytes());

        } catch (Exception e) {
            log.error("Failed to download image from URL: {}", url, e);
            throw new RuntimeException("Failed to download image");
        }
    }

    private List<FileImage> filterThumbsToSave(List<Product> products, User user) {
        List<FileImage> thumbsToSave = new ArrayList<>();
        for (Product product : products) {
            if (!product.hasThumbnails()) continue;

            String thumbName = randomThumbName();
            String path = composeFilePath(user.getId(), product.getId(), thumbName);

            FileImage image = product.getThumbnails().getFirst();
            image.setUri(path);
            image.setFileType("image/jpeg");
            image.setFileName(thumbName);
            thumbsToSave.add(image);
        }

        return thumbsToSave;
    }
}
