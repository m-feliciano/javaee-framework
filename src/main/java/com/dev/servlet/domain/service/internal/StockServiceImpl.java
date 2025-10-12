package com.dev.servlet.domain.service.internal;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.mapper.InventoryMapper;
import com.dev.servlet.domain.model.Inventory;
import com.dev.servlet.domain.model.Product;
import com.dev.servlet.domain.model.enums.Status;
import com.dev.servlet.domain.service.IBusinessService;
import com.dev.servlet.domain.service.IStockService;
import com.dev.servlet.domain.transfer.request.InventoryCreateRequest;
import com.dev.servlet.domain.transfer.request.InventoryRequest;
import com.dev.servlet.domain.transfer.response.InventoryResponse;
import com.dev.servlet.domain.transfer.response.ProductResponse;
import com.dev.servlet.infrastructure.persistence.dao.InventoryDAO;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Model;
import javax.inject.Inject;
import java.util.List;

import static com.dev.servlet.core.util.CryptoUtils.getUser;
import static com.dev.servlet.core.util.ThrowableUtils.notFound;

@Slf4j
@NoArgsConstructor
@Model
public class StockServiceImpl extends BaseServiceImpl<Inventory, String> implements IStockService {

    @Inject
    private IBusinessService businessService;

    @Inject
    private InventoryMapper inventoryMapper;

    @Inject
    public StockServiceImpl(InventoryDAO dao) {
        super(dao);
    }

    private InventoryDAO getDAO() {
        return (InventoryDAO) super.getBaseDAO();
    }

    @Override
    public InventoryResponse create(InventoryCreateRequest request, String auth) throws ServiceException {
        log.trace("");

        Inventory inventory = inventoryMapper.createToInventory(request);
        ProductResponse product = businessService.getProductById(inventory.getProduct().getId(), auth);
        inventory.setProduct(new Product(product.getId()));
        inventory.setStatus(Status.ACTIVE.getValue());
        inventory.setUser(getUser(auth));
        inventory = super.save(inventory);
        return inventoryMapper.toResponse(inventory);
    }

    @Override
    public List<InventoryResponse> list(InventoryRequest request, String auth) throws ServiceException {
        log.trace("");
        Inventory inventory = inventoryMapper.toInventory(request);
        inventory.setUser(getUser(auth));

        return findAll(inventory)
                .stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }

    @Override
    public InventoryResponse findById(InventoryRequest request, String auth) throws ServiceException {
        log.trace("");
        Inventory inventory = require(request.id());
        return inventoryMapper.toResponse(inventory);
    }

    @Override
    public InventoryResponse update(InventoryRequest request, String auth) throws ServiceException {
        log.trace("");

        Inventory inventory = require(request.id());
        ProductResponse product = businessService.getProductById(request.product().id(), auth);

        inventory.setProduct(new Product(product.getId()));
        inventory.setDescription(request.description());
        inventory.setQuantity(request.quantity());
        inventory.setStatus(Status.ACTIVE.getValue());
        super.update(inventory);
        return inventoryMapper.toResponse(inventory);
    }

    @Override
    public void delete(InventoryRequest request, String auth) throws ServiceException {
        log.trace("");
        Inventory inventory = require(request.id());
        super.delete(inventory);
    }

    @Override
    public boolean hasInventory(Inventory inventory, String auth) {
        log.trace("");
        inventory.setUser(getUser(auth));
        InventoryDAO DAO = this.getDAO();
        return DAO.has(inventory);
    }

    private Inventory require(String id) throws ServiceException {
        return this.findById(id).orElseThrow(() -> notFound("Inventory not found"));
    }
}
