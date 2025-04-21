package com.dev.servlet.model.shared;

import com.dev.servlet.model.InventoryModel;
import com.dev.servlet.model.ProductModel;
import com.dev.servlet.pojo.domain.Inventory;
import com.dev.servlet.pojo.domain.Product;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Product shared.
 * <p>
 * This class is used to share the product between the product and inventory business.
 * And will not be used in the view layer.
 *
 * @since 1.3.0
 */
@Singleton
public class BusinessShared {

    private ProductModel productModel;
    private InventoryModel inventoryModel;

    @Inject
    public void setProductModel(ProductModel productModel) {
        this.productModel = productModel;
    }

    @Inject
    public void setInventoryModel(InventoryModel inventoryModel) {
        this.inventoryModel = inventoryModel;
    }

    public BusinessShared() {
        // Empty constructor
    }

    public boolean hasInventory(Inventory inventory) {
        return inventoryModel.hasInventory(inventory);
    }

    /**
     * Find a product by id.
     *
     * @param productId
     * @return {@linkplain Product}
     */
    public Product getProductById(Long productId) {
        if (productId == null) return null;
        return productModel.find(new Product(productId));
    }
}
