package com.dev.servlet.application.port.in.product;

import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.shared.vo.KeyPair;

import java.util.Set;

public interface ListProductContainerUseCase {

    Set<KeyPair> assembleContainerResponse(IPageRequest pageRequest, String auth, Product product);
}