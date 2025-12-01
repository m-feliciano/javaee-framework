package com.dev.servlet.application.port.in.product;

import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.domain.valueobject.KeyPair;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;

import java.util.Set;

public interface ListProductContainerUseCasePort {

    Set<KeyPair> assembleContainerResponse(IPageRequest pageRequest, String auth, Product product);
}