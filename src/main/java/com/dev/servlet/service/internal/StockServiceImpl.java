package com.dev.servlet.service.internal;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.mapper.InventoryMapper;
import com.dev.servlet.core.util.JwtUtil;
import com.dev.servlet.domain.model.Inventory;
import com.dev.servlet.domain.model.Product;
import com.dev.servlet.domain.model.enums.Status;
import com.dev.servlet.service.AuditService;
import com.dev.servlet.service.IBusinessService;
import com.dev.servlet.service.IStockService;
import com.dev.servlet.domain.request.InventoryCreateRequest;
import com.dev.servlet.domain.request.InventoryRequest;
import com.dev.servlet.domain.response.InventoryResponse;
import com.dev.servlet.domain.response.ProductResponse;
import com.dev.servlet.infrastructure.persistence.dao.InventoryDAO;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Model;
import javax.inject.Inject;
import java.util.List;

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
    private AuditService auditService;

    @Inject
    private JwtUtil jwtUtil;

    @Inject
    public StockServiceImpl(InventoryDAO dao) {
        super(dao);
    }

    private InventoryDAO getDAO() {
        return (InventoryDAO) super.getBaseDAO();
    }

    @Override
    public InventoryResponse register(InventoryCreateRequest request, String auth) throws ServiceException {
        Inventory inventory = inventoryMapper.createToInventory(request);
        try {
            ProductResponse product = businessService.getProductDetail(inventory.getProduct().getId(), auth);
            inventory.setProduct(new Product(product.getId()));
            inventory.setStatus(Status.ACTIVE.getValue());
            inventory.setUser(jwtUtil.getUser(auth));
            inventory = super.save(inventory);

            InventoryResponse response = inventoryMapper.toResponse(inventory);
            auditService.auditSuccess("inventory:create", auth, new AuditPayload<>(request, response));
            return response;
        } catch (Exception e) {
            auditService.auditFailure("inventory:create", auth, new AuditPayload<>(request, null));
            throw e;
        }
    }

    @Override
    public List<InventoryResponse> list(InventoryRequest request, String auth) throws ServiceException {
        try {
            Inventory inventory = inventoryMapper.toInventory(request);
            inventory.setUser(jwtUtil.getUser(auth));

            List<InventoryResponse> responses = findAll(inventory)
                    .stream()
                    .map(inventoryMapper::toResponse)
                    .toList();
            auditService.auditSuccess("inventory:list", auth, new AuditPayload<>(request, responses));
            return responses;
        } catch (Exception e) {
            auditService.auditFailure("inventory:list", auth, new AuditPayload<>(request, null));
            throw e;
        }
    }

    @Override
    public InventoryResponse getStockDetail(InventoryRequest request, String auth) throws ServiceException {
        try {
            Inventory inventory = loadInventory(request.id());
            InventoryResponse response = inventoryMapper.toResponse(inventory);
            auditService.auditSuccess("inventory:find_by_id", auth, new AuditPayload<>(request, response));
            return response;
        } catch (Exception e) {
            auditService.auditFailure("inventory:find_by_id", auth, new AuditPayload<>(request, null));
            throw e;
        }
    }

    @Override
    public InventoryResponse update(InventoryRequest request, String auth) throws ServiceException {
        try {
            Inventory inventory = loadInventory(request.id());
            ProductResponse product = businessService.getProductDetail(request.product().id(), auth);

            inventory.setProduct(new Product(product.getId()));
            inventory.setDescription(request.description());
            inventory.setQuantity(request.quantity());
            inventory.setStatus(Status.ACTIVE.getValue());
            super.update(inventory);
            InventoryResponse response = inventoryMapper.toResponse(inventory);
            auditService.auditSuccess("inventory:update", auth, new AuditPayload<>(request, response));
            return response;
        } catch (Exception e) {
            auditService.auditFailure("inventory:update", auth, new AuditPayload<>(request, null));
            throw e;
        }
    }

    @Override
    public void delete(InventoryRequest request, String auth) throws ServiceException {
        try {
            Inventory inventory = loadInventory(request.id());
            super.delete(inventory);
            auditService.auditSuccess("inventory:delete", auth, new AuditPayload<>(request, null));
        } catch (Exception e) {
            auditService.auditFailure("inventory:delete", auth, new AuditPayload<>(request, null));
            throw e;
        }
    }

    @Override
    public boolean hasInventory(Inventory inventory, String auth) {
        try {
            inventory.setUser(jwtUtil.getUser(auth));
            InventoryDAO DAO = this.getDAO();
            boolean result = DAO.has(inventory);
            auditService.auditSuccess("inventory:has_inventory", auth, new AuditPayload<>(inventory, result));
            return result;
        } catch (Exception e) {
            auditService.auditFailure("inventory:has_inventory", auth, new AuditPayload<>(inventory, null));
            throw e;
        }
    }

    private Inventory loadInventory(String id) throws ServiceException {
        return this.findById(id).orElseThrow(() -> notFound("Inventory not found"));
    }
}
