package com.dev.servlet.core.mapper;

import com.dev.servlet.domain.model.Inventory;
import com.dev.servlet.domain.transfer.records.Query;
import com.dev.servlet.domain.transfer.request.CategoryRequest;
import com.dev.servlet.domain.transfer.request.InventoryCreateRequest;
import com.dev.servlet.domain.transfer.request.InventoryRequest;
import com.dev.servlet.domain.transfer.request.ProductRequest;
import com.dev.servlet.domain.transfer.response.InventoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN)
public interface InventoryMapper {
    InventoryResponse toResponse(Inventory inventory);

    Inventory toInventory(InventoryRequest inventoryResponse);

    @Mapping(target = "product", expression = "java(new com.dev.servlet.domain.model.Product(inventoryResponse.productId()))")
    Inventory createToInventory(InventoryCreateRequest inventoryResponse);

    default InventoryRequest queryToInventory(Query query) {
        InventoryRequest.InventoryRequestBuilder builder = InventoryRequest.builder();
        ProductRequest.ProductRequestBuilder productBuilder = ProductRequest.builder();

        query.queries().forEach((k, v) -> {
            if ("product".equals(k)) {
                productBuilder.id(v.trim());

            } else if ("name".equals(k)) {
                productBuilder.name(v.trim());

            } else if ("category".equals(k)) {
                productBuilder.category(CategoryRequest.builder().id(v.trim()).build());

            } else if ("description".equals(k)) {
                builder.description(v.trim());
            }
        });

        builder.product(productBuilder.build());
        return builder.build();
    }
}
