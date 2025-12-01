package com.dev.servlet.application.usecase.product;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.mapper.ProductMapper;
import com.dev.servlet.application.port.in.category.ListCategoryUseCasePort;
import com.dev.servlet.application.port.in.product.ListProductContainerUseCasePort;
import com.dev.servlet.application.port.in.product.ListProductUseCasePort;
import com.dev.servlet.application.port.in.product.ProductCalculatePriceUseCasePort;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import com.dev.servlet.application.transfer.response.ProductResponse;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.domain.valueobject.KeyPair;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.infrastructure.persistence.transfer.IPageable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class ListProductContainerUseCase implements ListProductContainerUseCasePort {

    @Inject
    private ListProductUseCasePort listProductUseCasePort;
    @Inject
    private ProductCalculatePriceUseCasePort productCalculatePriceUseCasePort;
    @Inject
    private ListCategoryUseCasePort listCategoryUseCasePort;
    @Inject
    private ProductMapper productMapper;

    public Set<KeyPair> assembleContainerResponse(IPageRequest pageRequest, String auth, Product product) throws ApplicationException {
        pageRequest.setFilter(product);
        IPageable<ProductResponse> page = listProductUseCasePort.getAllPageable(
                pageRequest, productMapper::toResponseWithoutCategory);

        BigDecimal price = productCalculatePriceUseCasePort.calculateTotalPriceFor(page, product);
        Collection<CategoryResponse> categories = listCategoryUseCasePort.list(null, auth);
        Set<KeyPair> container = new HashSet<>();

        container.add(new KeyPair("pageable", page));
        container.add(new KeyPair("totalPrice", price));
        container.add(new KeyPair("categories", categories));
        return container;
    }
}
