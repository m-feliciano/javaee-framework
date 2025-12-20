package com.dev.servlet.application.mapper;

import com.dev.servlet.application.transfer.request.CategoryRequest;
import com.dev.servlet.application.transfer.request.InventoryCreateRequest;
import com.dev.servlet.application.transfer.request.InventoryRequest;
import com.dev.servlet.application.transfer.request.ProductRequest;
import com.dev.servlet.application.transfer.response.InventoryResponse;
import com.dev.servlet.domain.entity.Inventory;
import com.dev.servlet.shared.vo.Query;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN)
public interface InventoryMapper {
    String PRODUCT_RESPONSE_TEMPLATE = """
                    java(new com.dev.servlet.application.transfer.response.ProductResponse(
                inventory.getProduct().getId(),
                inventory.getProduct().getName(),
                                inventory.getProduct().getPrice(),
                                inventory.getProduct().getThumbnail()
                )
            )""";

    @Mapping(target = "product", expression = PRODUCT_RESPONSE_TEMPLATE)
    InventoryResponse toResponse(Inventory inventory);

    Inventory toInventory(InventoryRequest inventoryRequest);

    @Mapping(target = "product", expression = "java(new com.dev.servlet.domain.entity.Product(inventoryResponse.productId()))")
    Inventory createToInventory(InventoryCreateRequest inventoryResponse);

    default InventoryRequest queryToInventory(Query query) {
        InventoryRequest.InventoryRequestBuilder builder = InventoryRequest.builder();
        ProductRequest.ProductRequestBuilder prodBuilder = ProductRequest.builder();
        String product = "product";
        String name = "name";
        String category = "category";
        String description = "description";

        query.parameters().forEach((k, v) -> {
            if (product.equals(k)) prodBuilder.id(v.trim());
            else {
                if (name.equals(k)) prodBuilder.name(v.trim());

                else if (category.equals(k)) prodBuilder.category(CategoryRequest.builder().id(v.trim()).build());

                else if (description.equals(k)) builder.description(v.trim());
            }
        });
        builder.product(prodBuilder.build());
        return builder.build();
    }
}
