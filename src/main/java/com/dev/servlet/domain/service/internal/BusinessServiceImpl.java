package com.dev.servlet.domain.service.internal;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.mapper.ProductMapper;
import com.dev.servlet.domain.model.Inventory;
import com.dev.servlet.domain.service.IBusinessService;
import com.dev.servlet.domain.service.IProductService;
import com.dev.servlet.domain.service.IStockService;
import com.dev.servlet.domain.transfer.response.ProductResponse;
import com.dev.servlet.domain.transfer.request.ProductRequest;
import lombok.NoArgsConstructor;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Optional;

import static com.dev.servlet.core.util.ThrowableUtils.notFound;

@Singleton
@NoArgsConstructor
public class BusinessServiceImpl implements IBusinessService {

    @Inject
    @Named("productService")
    private IProductService productService;

    @Inject
    private ProductMapper productMapper;

    @Inject
    private IStockService stockService;

    @Override
    public boolean hasInventory(Inventory inventory, String auth) {
        return stockService.hasInventory(inventory, auth);
    }

    @Override
    public ProductResponse getProductById(String id, String auth) throws ServiceException {
        ProductRequest productRequest = ProductRequest.builder().id(id).build();
        ProductResponse response = productService.findById(productRequest, auth);
        return Optional.ofNullable(response)
                .orElseThrow(() -> notFound("Product not found"));
    }
}
