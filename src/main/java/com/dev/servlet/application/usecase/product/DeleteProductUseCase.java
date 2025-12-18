package com.dev.servlet.application.usecase.product;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.mapper.ProductMapper;
import com.dev.servlet.application.port.in.product.DeleteProductPort;
import com.dev.servlet.application.port.in.stock.HasInventoryPort;
import com.dev.servlet.application.port.out.product.ProductRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.port.out.storage.StorageService;
import com.dev.servlet.application.transfer.request.ProductRequest;
import com.dev.servlet.domain.entity.Inventory;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.domain.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class DeleteProductUseCase implements DeleteProductPort {
    @Inject
    private ProductRepositoryPort repositoryPort;
    @Inject
    private ProductMapper productMapper;
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private HasInventoryPort hasInventoryPort;
    @Inject
    private StorageService storageService;

    @Override
    public void delete(ProductRequest request, String auth) throws AppException {
        log.debug("DeleteProductUseCase: deleting product with id {}", request.id());
        String userId = authenticationPort.extractUserId(auth);

        Product product = productMapper.toProduct(request, userId);
        product = repositoryPort.find(product).orElseThrow(() -> new AppException("Product not found"));

        Inventory inventory = Inventory.builder()
                .user(new User(userId))
                .product(new Product(product.getId()))
                .build();
        if (hasInventoryPort.hasInventory(inventory, auth)) {
            throw new AppException("Cannot delete product with existing inventory");
        }

        String thumb = product.getThumbnail();
        repositoryPort.delete(product);

        if (thumb != null && !thumb.isBlank()) {
            storageService.deleteFile(thumb);
        }
    }
}
