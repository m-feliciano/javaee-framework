package com.dev.servlet.application.usecase.product;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.mapper.ProductMapper;
import com.dev.servlet.application.port.in.category.ListCategoryPort;
import com.dev.servlet.application.port.in.product.ListProductContainerPort;
import com.dev.servlet.application.port.in.product.ProductCalculatePricePort;
import com.dev.servlet.application.port.out.product.ListProductPort;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import com.dev.servlet.application.transfer.response.ProductResponse;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.infrastructure.persistence.transfer.IPageable;
import com.dev.servlet.shared.vo.KeyPair;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@ApplicationScoped
public class ListProductContainerUseCase implements ListProductContainerPort {
    @Inject
    private ListProductPort listProductPort;
    @Inject
    private ProductCalculatePricePort productCalculatePricePort;
    @Inject
    private ListCategoryPort listCategoryPort;
    @Inject
    private ProductMapper productMapper;

    public Set<KeyPair> assembleContainerResponse(IPageRequest pageRequest, String auth, Product product) throws ApplicationException {
        pageRequest.setFilter(product);
        IPageable<ProductResponse> page = listProductPort.getAllPageable(
                pageRequest, auth, productMapper::toResponseWithoutCategory);

        BigDecimal price = productCalculatePricePort.calculateTotalPriceFor(page, product);
        Collection<CategoryResponse> categories = listCategoryPort.list(null, auth);
        Set<KeyPair> container = new HashSet<>();

        container.add(new KeyPair("pageable", page));
        container.add(new KeyPair("totalPrice", price));
        container.add(new KeyPair("categories", categories));
        return container;
    }
}
