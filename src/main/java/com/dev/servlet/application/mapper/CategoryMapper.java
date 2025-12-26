package com.dev.servlet.application.mapper;

import com.dev.servlet.application.transfer.request.CategoryRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import com.dev.servlet.domain.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN)
public interface CategoryMapper {
    @Mapping(source = "id", target = "id")
    CategoryResponse toResponse(Category category);

    Category toCategory(CategoryRequest request);
}
