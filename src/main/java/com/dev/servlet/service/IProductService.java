package com.dev.servlet.service;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.mapper.Mapper;
import com.dev.servlet.domain.model.Product;
import com.dev.servlet.domain.request.ProductRequest;
import com.dev.servlet.domain.response.ProductResponse;
import com.dev.servlet.infrastructure.persistence.IPageRequest;
import com.dev.servlet.infrastructure.persistence.IPageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IProductService {
    ProductResponse register(ProductRequest product, String auth) throws ServiceException;

    ProductResponse getProductDetail(ProductRequest product, String auth) throws ServiceException;

    ProductResponse update(ProductRequest product, String auth) throws ServiceException;

    void delete(ProductRequest product, String auth) throws ServiceException;

    BigDecimal calculateTotalPriceFor(IPageable<?> page, Product product);

    Optional<List<ProductResponse>> scrape(String url, String environment, String auth);

    <U> IPageable<U> getAllPageable(IPageRequest pageRequest, String auth, Mapper<Product, U> mapper);
}
