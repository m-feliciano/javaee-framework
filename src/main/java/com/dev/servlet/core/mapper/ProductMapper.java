package com.dev.servlet.core.mapper;

import com.dev.servlet.domain.model.Product;
import com.dev.servlet.domain.model.User;
import com.dev.servlet.domain.records.Query;
import com.dev.servlet.domain.request.CategoryRequest;
import com.dev.servlet.domain.request.ProductRequest;
import com.dev.servlet.domain.response.ProductResponse;
import com.dev.servlet.infrastructure.external.webscrape.transfer.ProductWebScrapeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ProductMapper {
    ProductResponse toResponse(Product product);

    @Mapping(target = "category", ignore = true)
    ProductResponse toResponseWithoutCategory(Product product);

    Product scrapeToProduct(ProductWebScrapeDTO productWebScrapeDTO);

    @Mapping(target = "user", expression = "java(new com.dev.servlet.domain.model.User(userId))")
    Product toProduct(ProductRequest product, String userId);

    default Product queryToProduct(Query query, User user) {
        ProductRequest.ProductRequestBuilder builder = ProductRequest.builder();

        query.queries().forEach((k, v) -> {
            if ("description".equals(k)) {
                builder.description(v.trim());
            } else if ("name".equals(k)) {
                builder.name(v.trim());
            } else if ("category".equals(k)) {
                builder.category(CategoryRequest.builder().id(v.trim()).build());
            }
        });

        return toProduct(builder.build(), user.getId());
    }
}