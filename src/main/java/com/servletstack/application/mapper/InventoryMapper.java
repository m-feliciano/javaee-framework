package com.servletstack.application.mapper;

import com.servletstack.application.transfer.request.CategoryRequest;
import com.servletstack.application.transfer.request.InventoryCreateRequest;
import com.servletstack.application.transfer.request.InventoryRequest;
import com.servletstack.application.transfer.request.ProductRequest;
import com.servletstack.application.transfer.response.InventoryResponse;
import com.servletstack.domain.entity.Inventory;
import com.servletstack.shared.vo.Query;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN)
public interface InventoryMapper {
    String PRODUCT_RESPONSE_TEMPLATE = """
                    java(new com.servletstack.application.transfer.response.ProductResponse(
                inventory.getProduct().getId(),
                inventory.getProduct().getName(),
                                inventory.getProduct().getPrice(),
                                inventory.getProduct().getThumbnail()
                )
            )""";

    @Mapping(target = "product", expression = PRODUCT_RESPONSE_TEMPLATE)
    InventoryResponse toResponse(Inventory inventory);

    Inventory toInventory(InventoryRequest inventoryRequest);

    @Mapping(target = "product", expression = "java(new com.servletstack.domain.entity.Product(inventoryResponse.productId()))")
    Inventory createToInventory(InventoryCreateRequest inventoryResponse);

    default InventoryRequest queryToInventory(Query query) {
        InventoryRequest.InventoryRequestBuilder builder = InventoryRequest.builder();
        ProductRequest.ProductRequestBuilder prodBuilder = ProductRequest.builder();
        String product = "product";
        String name = "name";
        String category = "category";
        String description = "description";

        query.parameters().forEach((k, v) -> {
            if (product.equals(k)) prodBuilder.id(UUID.fromString(v));
            else {
                if (name.equals(k)) prodBuilder.name(v.trim());

                else if (category.equals(k))
                    prodBuilder.category(
                            CategoryRequest.builder()
                                    .id(UUID.fromString(v.trim()))
                                    .build()
                    );

                else if (description.equals(k)) builder.description(v.trim());
            }
        });
        builder.product(prodBuilder.build());
        return builder.build();
    }
}
