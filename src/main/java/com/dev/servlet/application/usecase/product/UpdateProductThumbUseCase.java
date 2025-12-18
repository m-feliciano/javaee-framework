package com.dev.servlet.application.usecase.product;


import com.dev.servlet.adapter.out.image.ImageService;
import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.port.in.product.UpdateProductThumbPort;
import com.dev.servlet.application.port.out.image.FileImageRepositoryPort;
import com.dev.servlet.application.port.out.product.ProductRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.port.out.storage.StorageService;
import com.dev.servlet.application.transfer.request.FileUploadRequest;
import com.dev.servlet.domain.entity.FileImage;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.Status;
import com.dev.servlet.infrastructure.config.Properties;
import com.dev.servlet.infrastructure.http.FileHttpClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class UpdateProductThumbUseCase implements UpdateProductThumbPort {
    private static final long MAX_IMAGE_SIZE = 1024 * 1024;

    @Inject
    private AuthenticationPort authPort;
    @Inject
    private StorageService storageService;
    @Inject
    private ImageService imageService;
    @Inject
    private ProductRepositoryPort repository;
    @Inject
    private FileHttpClient fileHttpClient;
    @Inject
    private FileImageRepositoryPort fileImageRepositoryPort;

    @Override
    public void updateThumb(FileUploadRequest request, String auth) throws AppException {

        if (Properties.isDemoModeEnabled()) {
            throw new AppException("Operation not allowed in demo mode.");
        }

        if (request.payload().size() > MAX_IMAGE_SIZE) {
            throw new AppException("Image size exceeds 1MB.");
        }

        String userId = authPort.extractUserId(auth);
        String productId = Objects.requireNonNull(request.id(), "Product ID is required");

        Product product = Product.builder()
                .id(productId)
                .owner(new User(userId))
                .build();
        product = repository.find(product).orElseThrow(() -> new AppException("Product not found"));

        final String filename = UUID.randomUUID().toString().substring(0, 8) + ".jpg";
        final String path = "private/users/" + userId + "/products/" + productId + "/thumb/" + filename;

        if (product.hasThumbnails()) {
            FileImage image = product.getThumbnails().getFirst();

            String oldPath = image.getUri();
            image.setFileName(filename);
            image.setUri(path);
            fileImageRepositoryPort.update(image);
            storageService.deleteFile(oldPath);

        } else {
            FileImage image = FileImage.builder()
                    .uri(path)
                    .fileName(filename).fileType("image/jpeg")
                    .status(Status.ACTIVE.getValue())
                    .build();
            product.addThumbnail(image);
            fileImageRepositoryPort.save(image);
        }

        URI uploadUri = storageService.generateUploadUri(path, "image/jpeg", Duration.ofMinutes(5));

        try (InputStream raw = request.payload().openStream();
             InputStream processed = imageService.processToOptimizedJpg(raw, 400, "product-thumbnail")) {

            fileHttpClient.upload(uploadUri, processed, "image/jpeg");

        } catch (Exception e) {
            log.error("Failed to update product thumbnail", e);
            throw new AppException("Failed to update product thumbnail");

        } finally {
            request.payload().close();
        }
    }
}
