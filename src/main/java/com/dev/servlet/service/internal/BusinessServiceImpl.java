package com.dev.servlet.service.internal;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.domain.model.Inventory;
import com.dev.servlet.domain.request.ProductRequest;
import com.dev.servlet.domain.response.ProductResponse;
import com.dev.servlet.service.IBusinessService;
import com.dev.servlet.service.IProductService;
import com.dev.servlet.service.IStockService;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NoArgsConstructor;

@Singleton
@NoArgsConstructor
public class BusinessServiceImpl implements IBusinessService {

    @Inject
    @Named("productService")
    private IProductService productService;

    @Inject
    private IStockService stockService;

    @Override
    public boolean hasInventory(Inventory inventory, String auth) {
        return stockService.hasInventory(inventory, auth);
    }

    @Override
    public ProductResponse getProductDetail(String id, String auth) throws ServiceException {
        ProductRequest request = ProductRequest.builder().id(id).build();
        return productService.findById(request, auth);
    }
}
