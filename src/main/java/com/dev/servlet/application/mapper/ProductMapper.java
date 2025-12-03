package com.dev.servlet.application.mapper;

import com.dev.servlet.adapter.out.external.webscrape.transfer.ProductWebScrapeDTO;
import com.dev.servlet.application.transfer.request.CategoryRequest;
import com.dev.servlet.application.transfer.request.ProductRequest;
import com.dev.servlet.application.transfer.response.ProductResponse;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.shared.vo.Query;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ProductMapper {
    ProductResponse toResponse(Product product);

    @Mapping(target = "category", ignore = true)
    ProductResponse toResponseWithoutCategory(Product product);

    Product scrapeToProduct(ProductWebScrapeDTO productWebScrapeDTO);

    @Mapping(target = "user", expression = "java(new com.dev.servlet.domain.entity.User(userId))")
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
