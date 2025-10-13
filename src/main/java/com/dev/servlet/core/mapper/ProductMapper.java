package com.dev.servlet.core.mapper;

import com.dev.servlet.domain.model.Product;
import com.dev.servlet.domain.transfer.records.KeyPair;
import com.dev.servlet.domain.transfer.records.Query;
import com.dev.servlet.domain.transfer.request.CategoryRequest;
import com.dev.servlet.domain.transfer.request.ProductRequest;
import com.dev.servlet.domain.transfer.response.ProductResponse;
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

    @Mapping(target = "user", expression = "java(com.dev.servlet.core.util.CryptoUtils.getUser(auth))")
    Product toProduct(ProductRequest product, String auth);

    default Product queryToProduct(Query query, String auth) {
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

        return toProduct(builder.build(), auth);
    }
}