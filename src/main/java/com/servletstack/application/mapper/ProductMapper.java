package com.servletstack.application.mapper;

import com.servletstack.adapter.out.external.webscrape.transfer.ProductWebScrapeDTO;
import com.servletstack.application.transfer.request.CategoryRequest;
import com.servletstack.application.transfer.request.ProductRequest;
import com.servletstack.application.transfer.response.ProductResponse;
import com.servletstack.domain.entity.FileImage;
import com.servletstack.domain.entity.Product;
import com.servletstack.domain.entity.User;
import com.servletstack.shared.vo.Query;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ProductMapper {
    @Mapping(target = "thumbUrl", source = "thumbnails", qualifiedByName = "firstElement")
    ProductResponse toResponse(Product product);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "thumbUrl", source = "thumbnails", qualifiedByName = "firstElement")
    ProductResponse toResponseWithoutCategory(Product product);

    Product scrapeToProduct(ProductWebScrapeDTO productWebScrapeDTO);

    @Mapping(target = "owner", expression = "java(new com.servletstack.domain.entity.User(userId))")
    Product toProduct(ProductRequest product, UUID userId);

    default Product queryToProduct(Query query, User user) {
        ProductRequest.ProductRequestBuilder builder = ProductRequest.builder();
        query.parameters().forEach((k, v) -> {
            if ("description".equals(k)) {
                builder.description(v.trim());
            } else if ("name".equals(k)) {
                builder.name(v.trim());
            } else if ("category".equals(k)) {
                builder.category(
                        CategoryRequest.builder()
                                .id(UUID.fromString(v))
                                .build()
                );
            }
        });
        return toProduct(builder.build(), user.getId());
    }

    @Named("firstElement")
    default String firstElement(java.util.List<FileImage> list) {
        if (list != null && !list.isEmpty()) {
            return list.getFirst().getUri();
        }
        return null;
    }
}
