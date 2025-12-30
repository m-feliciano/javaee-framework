package com.servletstack.application.usecase.product;


import com.servletstack.adapter.out.image.ImageService;
import com.servletstack.application.exception.AppException;
import com.servletstack.application.exception.NotFoundException;
import com.servletstack.application.port.in.product.UpdateProductThumbUseCase;
import com.servletstack.application.port.out.image.FileImageRepositoryPort;
import com.servletstack.application.port.out.product.ProductRepositoryPort;
import com.servletstack.application.port.out.security.AuthenticationPort;
import com.servletstack.application.port.out.storage.StorageService;
import com.servletstack.application.transfer.request.FileUploadRequest;
import com.servletstack.domain.entity.FileImage;
import com.servletstack.domain.entity.Product;
import com.servletstack.domain.entity.User;
import com.servletstack.domain.entity.enums.Status;
import com.servletstack.infrastructure.config.Properties;
import com.servletstack.infrastructure.http.FileHttpClient;
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
public class UpdateProductThumbService implements UpdateProductThumbUseCase {
    private static final long MAX_IMAGE_SIZE = 1024 * 1024;

    @Inject
    private AuthenticationPort auth;
    @Inject
    private StorageService storage;
    @Inject
    private ImageService imageService;
    @Inject
    private ProductRepositoryPort repository;
    @Inject
    private FileHttpClient fileClient;
    @Inject
    private FileImageRepositoryPort fileImageRepository;

    @Override
    public void updateThumb(FileUploadRequest request, String auth) throws AppException {

        if (Properties.isDemoModeEnabled()) {
            throw new AppException("Operation not allowed in demo mode.");
        }

        if (request.payload().size() > MAX_IMAGE_SIZE) {
            throw new AppException("Image size exceeds 1MB.");
        }

        UUID userId = this.auth.extractUserId(auth);
        UUID productId = Objects.requireNonNull(request.id(), "Product ID is required");

        Product product = Product.builder()
                .id(productId)
                .owner(new User(userId))
                .build();
        product = repository.find(product).orElseThrow(NotFoundException::new);

        final String filename = UUID.randomUUID().toString().substring(0, 8) + ".jpg";
        final String path = "private/users/" + userId + "/products/" + productId + "/thumb/" + filename;

        if (product.hasThumbnails()) {
            FileImage image = product.getThumbnails().getFirst();

            String oldPath = image.getUri();
            image.setFileName(filename);
            image.setUri(path);
            fileImageRepository.update(image);
            storage.deleteFile(oldPath);

        } else {
            FileImage image = FileImage.builder()
                    .uri(path)
                    .fileName(filename).fileType("image/jpeg")
                    .status(Status.ACTIVE.getValue())
                    .build();
            product.addThumbnail(image);
            fileImageRepository.save(image);
        }

        URI uploadUri = storage.generateUploadUri(path, "image/jpeg", Duration.ofMinutes(5));

        try (InputStream raw = request.payload().openStream();
             InputStream processed = imageService.processToOptimizedJpg(raw, 400, "product-thumbnail")) {

            fileClient.upload(uploadUri, processed, "image/jpeg");

        } catch (Exception e) {
            log.error("Failed to update product thumbnail", e);
            throw new AppException("Failed to update product thumbnail");

        } finally {
            request.payload().close();
        }
    }
}
