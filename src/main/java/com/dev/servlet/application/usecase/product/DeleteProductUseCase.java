package com.dev.servlet.application.usecase.product;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.mapper.ProductMapper;
import com.dev.servlet.application.port.in.product.DeleteProductPort;
import com.dev.servlet.application.port.in.stock.HasInventoryPort;
import com.dev.servlet.application.port.out.product.ProductRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.request.ProductRequest;
import com.dev.servlet.domain.entity.Inventory;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.domain.entity.enums.Status;
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

    @Override
    public void delete(ProductRequest request, String auth) throws ApplicationException {
        log.debug("DeleteProductUseCase: deleting product with id {}", request.id());
        Product product = productMapper.toProduct(request, authenticationPort.extractUserId(auth));
        product.setStatus(Status.ACTIVE.getValue());
        product = repositoryPort.find(product)
                .orElseThrow(() -> new ApplicationException("Product not found"));

        if (hasInventoryPort.hasInventory(
                Inventory.builder()
                        .user(authenticationPort.extractUser(auth))
                        .product(new Product(product.getId()))
                        .build()
                , auth)) {
            throw new ApplicationException("Cannot delete product with existing inventory");
        }

        repositoryPort.delete(product);
    }
}
