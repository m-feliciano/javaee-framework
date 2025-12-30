package com.servletstack.application.usecase.stock;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.exception.NotFoundException;
import com.servletstack.application.mapper.InventoryMapper;
import com.servletstack.application.port.in.stock.GetInventoryDetailUseCase;
import com.servletstack.application.port.out.inventory.InventoryRepositoryPort;
import com.servletstack.application.transfer.request.InventoryRequest;
import com.servletstack.application.transfer.response.InventoryResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@ApplicationScoped
public class GetInventoryDetailService implements GetInventoryDetailUseCase {
    @Inject
    private InventoryMapper mapper;
    @Inject
    private InventoryRepositoryPort repository;

    @Override
    public InventoryResponse get(InventoryRequest request, String auth) throws AppException {
        log.debug("GetInventoryDetailUseCase: attempting to get inventory detail with id {}", request.id());

        return repository.findById(request.id())
                .map(mapper::toResponse)
                .orElseThrow(NotFoundException::new);
    }
}
