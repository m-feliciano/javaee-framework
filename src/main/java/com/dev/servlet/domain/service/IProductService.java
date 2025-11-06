package com.dev.servlet.domain.service;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.mapper.Mapper;
import com.dev.servlet.domain.model.Product;
import com.dev.servlet.domain.transfer.request.ProductRequest;
import com.dev.servlet.domain.transfer.response.ProductResponse;
import com.dev.servlet.infrastructure.persistence.IPageRequest;
import com.dev.servlet.infrastructure.persistence.IPageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IProductService {
    ProductResponse create(ProductRequest product, String auth) throws ServiceException;

    ProductResponse findById(ProductRequest product, String auth) throws ServiceException;

    ProductResponse update(ProductRequest product, String auth) throws ServiceException;

    void delete(ProductRequest product, String auth) throws ServiceException;

    BigDecimal calculateTotalPriceFor(Product product);

    Optional<List<ProductResponse>> scrape(String url, String environment, String auth);

    <U> IPageable<U> getAllPageable(IPageRequest pageRequest, Mapper<Product, U> mapper);
}
