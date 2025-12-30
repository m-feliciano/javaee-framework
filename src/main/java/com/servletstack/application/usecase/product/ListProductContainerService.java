package com.servletstack.application.usecase.product;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.mapper.ProductMapper;
import com.servletstack.application.port.in.category.ListCategoryUseCase;
import com.servletstack.application.port.in.product.ListProductContainerUseCase;
import com.servletstack.application.port.in.product.ProductCalculatePricePort;
import com.servletstack.application.port.out.product.ListProductPort;
import com.servletstack.application.transfer.response.CategoryResponse;
import com.servletstack.application.transfer.response.ProductResponse;
import com.servletstack.domain.entity.Product;
import com.servletstack.infrastructure.persistence.transfer.IPageRequest;
import com.servletstack.infrastructure.persistence.transfer.IPageable;
import com.servletstack.shared.vo.KeyPair;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@ApplicationScoped
public class ListProductContainerService implements ListProductContainerUseCase {
    @Inject
    private ListProductPort listProductPort;
    @Inject
    private ProductCalculatePricePort productCalculatePricePort;
    @Inject
    private ListCategoryUseCase listCategoryUseCase;
    @Inject
    private ProductMapper mapper;

    @Override
    public Set<KeyPair> assembleContainerResponse(IPageRequest pageRequest, String auth, Product product) throws AppException {
        pageRequest.setFilter(product);
        IPageable<ProductResponse> page = listProductPort.getAllPageable(
                pageRequest, auth, mapper::toResponseWithoutCategory);

        BigDecimal price = productCalculatePricePort.calculateTotalPriceFor(page, product);
        Collection<CategoryResponse> categories = listCategoryUseCase.list(null, auth);
        Set<KeyPair> container = new HashSet<>();

        container.add(new KeyPair("pageable", page));
        container.add(new KeyPair("totalPrice", price));
        container.add(new KeyPair("categories", categories));
        return container;
    }
}
