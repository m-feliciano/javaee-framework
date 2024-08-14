package com.dev.servlet.view;

import com.dev.servlet.controllers.InventoryController;
import com.dev.servlet.controllers.ProductController;
import com.dev.servlet.domain.Inventory;
import com.dev.servlet.domain.Product;
import com.dev.servlet.dto.ProductDto;
import com.dev.servlet.mapper.ProductMapper;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ProductShared {

    @Inject
    private ProductController productController;
    @Inject
    private InventoryController inventoryController;

    public ProductShared() {
    }

    public ProductShared(ProductController productController,
                         InventoryController inventoryController) {
        this.productController = productController;
        this.inventoryController = inventoryController;
    }

    public boolean hasInventory(Inventory inventory) {
        return inventoryController.hasInventory(inventory);
    }

    public ProductDto find(Long productId) {
        Product entity = productController.find(new Product(productId));
        return ProductMapper.from(entity);
    }
}
