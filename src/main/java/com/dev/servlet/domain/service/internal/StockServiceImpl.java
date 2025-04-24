package com.dev.servlet.domain.service.internal;
import com.dev.servlet.domain.service.IBusinessService;
import com.dev.servlet.domain.transfer.dto.InventoryDTO;
import com.dev.servlet.domain.transfer.request.Request;
import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.mapper.InventoryMapper;
import com.dev.servlet.domain.model.Category;
import com.dev.servlet.domain.model.Inventory;
import com.dev.servlet.domain.model.Product;
import com.dev.servlet.domain.model.enums.Status;
import com.dev.servlet.domain.service.IStockService;
import com.dev.servlet.infrastructure.persistence.dao.InventoryDAO;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import static com.dev.servlet.core.util.CryptoUtils.getUser;
import static com.dev.servlet.core.util.ThrowableUtils.notFound;

@Slf4j
@NoArgsConstructor
@Model
public class StockServiceImpl extends BaseServiceImpl<Inventory, String> implements IStockService {

    @Inject
    private IBusinessService businessService;
    @Inject
    public StockServiceImpl(InventoryDAO dao) {
        super(dao);
    }

    @Override
    public Class<InventoryDTO> getDataMapper() {
        return InventoryDTO.class;
    }
    @Override
    public Inventory toEntity(Object object) {
        return InventoryMapper.full((InventoryDTO) object);
    }

    private InventoryDAO getDAO() {
        return (InventoryDAO) super.getBaseDAO();
    }

    public Inventory getBody(Request request) {
        log.trace("");
        Inventory inventory = requestBody(request.getBody()).orElse(new Inventory());
        String parameter = request.getParameter("productId");
        if (parameter != null && !parameter.isEmpty()) {
            inventory.setProduct(new Product(parameter));
        }

        if (request.getQuery().getType() != null && request.getQuery().getSearch() != null) {
            Product product = new Product();
            if ("product".equals(request.getQuery().getType())) {
                product.setId(request.getQuery().getSearch().trim());
                inventory.setProduct(product);

            } else if ("name".equals(request.getQuery().getType())) {
                product.setName(request.getQuery().getSearch().trim());
                inventory.setProduct(product);

            } else {
                inventory.setDescription(request.getQuery().getSearch().trim());
            }
            String categoryId = request.getParameter("category");
            if (categoryId != null && !categoryId.isEmpty()) {
                product.setCategory(new Category(categoryId));
            }
        }
        inventory.setUser(getUser(request.getToken()));
        return inventory;
    }
    @Override
    public InventoryDTO create(Request request) throws ServiceException {
        log.trace("");
        Inventory inventory = this.getBody(request);
        inventory.setProduct(businessService.getProductById(inventory.getProduct().getId()));
        inventory.setStatus(Status.ACTIVE.getValue());
        inventory = super.save(inventory);
        return InventoryMapper.full(inventory);
    }
    @Override
    public List<InventoryDTO> list(Request request) {
        log.trace("");
        return this.findAll(request);
    }
    @Override
    public InventoryDTO findById(Request request) throws ServiceException {
        log.trace("");
        Inventory inventory = require(request.id());
        return InventoryMapper.full(inventory);
    }
    @Override
    public InventoryDTO update(Request request) throws ServiceException {
        log.trace("");
        Inventory inventory = require(request.id());

        Inventory body = this.getBody(request);
        inventory.setProduct(businessService.getProductById(body.getProduct().getId()));
        inventory.setDescription(body.getDescription());
        inventory.setQuantity(body.getQuantity());
        inventory.setStatus(Status.ACTIVE.getValue());
        super.update(inventory);
        return InventoryMapper.full(inventory);
    }

    @Override
    public boolean delete(Request request) throws ServiceException {
        log.trace("");
        Inventory inventory = require(request.id());
        return super.delete(inventory);
    }

    public boolean hasInventory(Inventory inventory) {
        return this.getDAO().has(inventory);
    }

    private List<InventoryDTO> findAll(Request request) {
        log.trace("");
        Inventory inventory = this.getBody(request);
        Collection<Inventory> inventories = super.findAll(inventory);
        return inventories.stream().map(InventoryMapper::full).toList();
    }

    private Inventory require(String id) throws ServiceException {
        return this.findById(id).orElseThrow(() -> notFound("Inventory not found"));
    }
}
