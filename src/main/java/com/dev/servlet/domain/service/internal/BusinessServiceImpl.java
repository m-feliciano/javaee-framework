package com.dev.servlet.domain.service.internal;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.domain.model.Inventory;
import com.dev.servlet.domain.model.Product;
import com.dev.servlet.domain.service.IBusinessService;
import com.dev.servlet.domain.service.IProductService;
import com.dev.servlet.domain.service.IStockService;
import lombok.NoArgsConstructor;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static com.dev.servlet.core.util.ThrowableUtils.notFound;

@Singleton
@NoArgsConstructor
public class BusinessServiceImpl implements IBusinessService {

    @Inject
    @Named("productService")
    private IProductService productService;
    @Inject
    private IStockService stockService;

    @Override
    public boolean hasInventory(Inventory inventory) {
        return stockService.hasInventory(inventory);
    }

    @Override
    public Product getProductById(String id) throws ServiceException {
        return productService.find(new Product(id))
                .orElseThrow(() -> notFound("Product not found"));
    }
}
