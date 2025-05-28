package com.dev.servlet.domain.service;

import com.dev.servlet.domain.transfer.dto.ProductDTO;
import com.dev.servlet.domain.transfer.request.Request;
import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.domain.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IProductService extends IBaseService<Product, Long> {
    ProductDTO create(Request request);
    List<Product> save(List<Product> products, String authorization) throws ServiceException;
    ProductDTO findById(Request request) throws ServiceException;
    ProductDTO update(Request request) throws ServiceException;
    boolean delete(Request request) throws ServiceException;
    BigDecimal calculateTotalPriceFor(Product product);
    Optional<List<ProductDTO>> scrape(Request request, String url);
}
