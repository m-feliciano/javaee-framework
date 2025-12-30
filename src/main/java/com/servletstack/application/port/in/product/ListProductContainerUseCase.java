package com.servletstack.application.port.in.product;

import com.servletstack.domain.entity.Product;
import com.servletstack.infrastructure.persistence.transfer.IPageRequest;
import com.servletstack.shared.vo.KeyPair;

import java.util.Set;

public interface ListProductContainerUseCase {

    Set<KeyPair> assembleContainerResponse(IPageRequest pageRequest, String auth, Product product);
}