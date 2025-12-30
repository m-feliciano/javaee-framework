package com.servletstack.application.usecase.product;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.exception.NotFoundException;
import com.servletstack.application.mapper.ProductMapper;
import com.servletstack.application.port.in.product.DeleteProductUseCase;
import com.servletstack.application.port.in.stock.HasInventoryUseCase;
import com.servletstack.application.port.out.product.ProductRepositoryPort;
import com.servletstack.application.port.out.security.AuthenticationPort;
import com.servletstack.application.port.out.storage.StorageService;
import com.servletstack.application.transfer.request.ProductRequest;
import com.servletstack.domain.entity.Inventory;
import com.servletstack.domain.entity.Product;
import com.servletstack.domain.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@ApplicationScoped
public class DeleteProductService implements DeleteProductUseCase {
    @Inject
    private ProductRepositoryPort repository;
    @Inject
    private ProductMapper mapper;
    @Inject
    private AuthenticationPort auth;
    @Inject
    private HasInventoryUseCase hasInventoryUseCase;
    @Inject
    private StorageService storage;

    @Override
    public void delete(ProductRequest request, String auth) throws AppException {
        log.debug("DeleteProductUseCase: deleting product with id {}", request.id());
        UUID userId = this.auth.extractUserId(auth);

        Product product = mapper.toProduct(request, userId);
        product = repository.find(product).orElseThrow(NotFoundException::new);

        Inventory inventory = Inventory.builder()
                .user(new User(userId))
                .product(new Product(product.getId()))
                .build();
        if (hasInventoryUseCase.hasInventory(inventory, auth)) {
            throw new AppException("Cannot delete product with existing inventory");
        }

        String thumb = product.getThumbnail();
        repository.delete(product);

        if (thumb != null && !thumb.isBlank()) {
            storage.deleteFile(thumb);
        }
    }
}
