package com.servletstack.application.mapper;

import com.servletstack.application.transfer.request.CategoryRequest;
import com.servletstack.application.transfer.response.CategoryResponse;
import com.servletstack.domain.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN)
public interface CategoryMapper {
    @Mapping(source = "id", target = "id")
    CategoryResponse toResponse(Category category);

    Category toCategory(CategoryRequest request);
}
