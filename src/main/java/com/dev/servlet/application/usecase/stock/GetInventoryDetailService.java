package com.dev.servlet.application.usecase.stock;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.exception.NotFoundException;
import com.dev.servlet.application.mapper.InventoryMapper;
import com.dev.servlet.application.port.in.stock.GetInventoryDetailUseCase;
import com.dev.servlet.application.port.out.inventory.InventoryRepositoryPort;
import com.dev.servlet.application.transfer.request.InventoryRequest;
import com.dev.servlet.application.transfer.response.InventoryResponse;
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
