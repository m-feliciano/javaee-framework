package com.dev.servlet.domain.service.internal;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.mapper.ProductMapper;
import com.dev.servlet.domain.model.Category;
import com.dev.servlet.domain.model.Inventory;
import com.dev.servlet.domain.model.Product;
import com.dev.servlet.domain.model.User;
import com.dev.servlet.domain.model.enums.Status;
import com.dev.servlet.domain.service.IBusinessService;
import com.dev.servlet.domain.service.IProductService;
import com.dev.servlet.domain.transfer.dto.DataTransferObject;
import com.dev.servlet.domain.transfer.dto.ProductDTO;
import com.dev.servlet.domain.transfer.records.Query;
import com.dev.servlet.domain.transfer.request.Request;
import com.dev.servlet.infrastructure.external.webscrape.WebScrapeServiceRegistry;
import com.dev.servlet.infrastructure.external.webscrape.builder.WebScrapeBuilder;
import com.dev.servlet.infrastructure.external.webscrape.transfer.ProductWebScrapeDTO;
import com.dev.servlet.infrastructure.persistence.dao.ProductDAO;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.dev.servlet.core.util.CryptoUtils.getUser;
import static com.dev.servlet.core.util.ThrowableUtils.notFound;

@Slf4j
@NoArgsConstructor
@Model
@Named("productService")
public class ProductServiceImpl extends BaseServiceImpl<Product, String> implements IProductService {
    @Inject
    private IBusinessService businessService;
    @Inject
    private WebScrapeServiceRegistry webScrapeServiceRegistry;

    @Inject
    public ProductServiceImpl(ProductDAO dao) {
        super(dao);
    }

    public ProductDAO getDAO() {
        return (ProductDAO) super.getBaseDAO();
    }

    @Override
    public Class<? extends DataTransferObject<String>> getDataMapper() {
        return ProductDTO.class;
    }

    @Override
    public Product toEntity(Object object) {
        return ProductMapper.full((ProductDTO) object);
    }

    @Override
    public Product getBody(Request request) {
        Product product = requestBody(request.getBody()).orElse(new Product());

        String categoryId = request.getParameter("category");
        if (categoryId != null && !categoryId.isBlank()) {
            product.setCategory(new Category(categoryId));
        }

        Query query = request.getQuery();
        if (query.getSearch() != null && query.getType() != null) {
            if (query.getType().equals("name")) {
                product.setName(query.getSearch());
            } else if (query.getType().equals("description")) {
                product.setDescription(query.getSearch());
            }
        }
        product.setUser(getUser(request.getToken()));
        return product;
    }

    @Override
    public ProductDTO create(Request request) {
        log.trace("");
        Product product = this.getBody(request);
        product.setRegisterDate(new Date());
        product.setStatus(Status.ACTIVE.getValue());
        product = super.save(product);
        return ProductMapper.full(product);
    }

    @Override
    public ProductDTO findById(Request request) throws ServiceException {
        log.trace("");
        Product product = require(request.id());
        return ProductMapper.full(product);
    }

    @Override
    public ProductDTO update(Request request) throws ServiceException {
        log.trace("");
        Product product = require(request.id());

        Product body = getBody(request);
        product.setName(body.getName());
        product.setDescription(body.getDescription());
        product.setPrice(body.getPrice());
        product.setUrl(body.getUrl());
        product.setCategory(body.getCategory());
        super.update(product);
        return ProductMapper.full(product);
    }

    @Override
    public boolean delete(Request request) throws ServiceException {
        log.trace("");
        Product product = require(request.id());
        Inventory inventory = Inventory.builder().user(product.getUser()).product(product).build();
        if (businessService.hasInventory(inventory)) {
            throw new ServiceException(HttpServletResponse.SC_CONFLICT, "Product has inventory.");
        }
        super.delete(product);
        return true;
    }

    @Override
    public BigDecimal calculateTotalPriceFor(Product product) {
        return this.getDAO().calculateTotalPriceFor(product);
    }

    @Override
    @SneakyThrows
    public Optional<List<ProductDTO>> scrape(Request request, String url) {
        log.trace("");
        final User user = getUser(request.getToken());

        Optional<List<ProductWebScrapeDTO>> scrapeResponse = WebScrapeBuilder.<List<ProductWebScrapeDTO>>create()
                .withServiceType("product")
                .withUrl(url)
                .withRegistry(webScrapeServiceRegistry)
                .execute();
        if (scrapeResponse.isEmpty()) {
            log.warn("No products found in the web scrape response");
            return Optional.empty();
        }

        List<ProductWebScrapeDTO> response = scrapeResponse.get();
        log.info("Web scrape returned {} products", response.size());

        List<Product> products = response.stream()
                .map(ProductMapper::fromWebScrapeDTO)
                .map(product -> prepareProductToSave(product, user))
                .toList();
        try {
            products = baseDAO.save(products);
            return Optional.of(products.stream().map(ProductMapper::base).toList());

        } catch (Exception e) {
            log.error("Error saving scraped products: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    private static Product prepareProductToSave(Product product, User user) {
        Date now = new Date();
        product.setRegisterDate(now);
        product.setStatus(Status.ACTIVE.getValue());
        product.setUser(user);
        return product;
    }

    private Product require(String id) throws ServiceException {
        return this.findById(id).orElseThrow(() -> notFound("Product not found"));
    }
}
