package com.dev.servlet.application.usecase.product;

import com.dev.servlet.adapter.out.storage.ImageService;
import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.port.in.product.UpdateProductThumbPort;
import com.dev.servlet.application.port.out.product.ProductRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.port.out.storage.StorageService;
import com.dev.servlet.application.transfer.request.FileUploadRequest;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.Status;
import com.dev.servlet.domain.vo.BinaryPayload;
import com.dev.servlet.infrastructure.config.Properties;
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
    private static final long MAX_IMAGE_SIZE = 1024 * 1024; // 1MB

    @Inject
    private AuthenticationPort authPort;
    @Inject
    private StorageService storageService;
    @Inject
    private ImageService imageService;
    @Inject
    private ProductRepositoryPort repositoryPort;

    public void updateThumb(FileUploadRequest request, String auth) throws AppException {
        if (Properties.isDemoModeEnabled()) {
            throw new AppException("Operation not allowed in demo mode.");
        }

        BinaryPayload payload = request.payload();
        if (payload.size() > MAX_IMAGE_SIZE) {
            throw new AppException("Image size exceeds 1MB.");
        }

        final String userId = authPort.extractUserId(auth);
        log.debug("Updating product thumbnail. userId={}", userId);

        final String productId = request.id();
        Objects.requireNonNull(productId, "Product ID must not be null");

        Product product = findProduct(productId, userId);
        final String oldThumbPath = product.getThumbUrl();
        final String path = "private/users/" + userId + "/products/" + productId + "/thumb/" + UUID.randomUUID() + ".jpg";

        URI uploadUri = storageService.generateUploadUri(path, "image/jpeg", Duration.ofMinutes(1));
        log.debug("Uploading product thumbnail to storage. productId={} uploadUri={}", productId, uploadUri);

        try (InputStream in = payload.openStream();
             InputStream pic = imageService.writePicture(in, 500)) {

            imageService.uploadImageToUrl(uploadUri, pic, "image/jpeg");
            product.setThumbUrl(path);
        } catch (Exception e) {
            log.error("Error updating product thumbnail", e);
            throw new AppException("Failed to update product thumbnail");

        } finally {
            payload.close();
        }

        repositoryPort.update(product);

        if (oldThumbPath != null) storageService.deleteFile(oldThumbPath);
    }

    private Product findProduct(String productId, String userId) {
        Product filter = Product.builder()
                .id(productId)
                .owner(new User(userId))
                .status(Status.ACTIVE.getValue())
                .build();

        return repositoryPort.find(filter)
                .orElseThrow(() -> new AppException("Product not found with id: " + productId));
    }
}
