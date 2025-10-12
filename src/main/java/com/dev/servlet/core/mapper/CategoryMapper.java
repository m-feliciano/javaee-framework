package com.dev.servlet.core.mapper;

import com.dev.servlet.domain.model.Category;
import com.dev.servlet.domain.transfer.response.CategoryResponse;
import com.dev.servlet.domain.transfer.request.CategoryRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN)
public interface CategoryMapper {
    CategoryResponse toResponse(Category category);

    Category toCategory(CategoryRequest request);
}
