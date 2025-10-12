package com.dev.servlet.domain.service.internal;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.mapper.ProductMapper;
import com.dev.servlet.core.response.HttpResponse;
import com.dev.servlet.domain.model.Category;
import com.dev.servlet.domain.model.Inventory;
import com.dev.servlet.domain.model.Product;
import com.dev.servlet.domain.model.User;
import com.dev.servlet.domain.model.enums.Status;
import com.dev.servlet.domain.service.IBusinessService;
import com.dev.servlet.domain.service.IProductService;
import com.dev.servlet.domain.transfer.request.ProductRequest;
import com.dev.servlet.domain.transfer.response.ProductResponse;
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
    private ProductMapper productMapper;

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
    public ProductResponse create(ProductRequest request, String auth) {
        log.trace("");

        Product product = productMapper.toProduct(request, auth);
        product.setRegisterDate(new Date());
        product.setStatus(Status.ACTIVE.getValue());
        product = super.save(product);
        return productMapper.toResponse(product);
    }

    @Override
    public ProductResponse findById(ProductRequest request, String auth) throws ServiceException {
        log.trace("");

        Product product = findProduct(productMapper.toProduct(request, auth));
        return productMapper.toResponse(product);
    }

    @Override
    public ProductResponse update(ProductRequest request, String auth) throws ServiceException {
        log.trace("");

        Product product = findProduct(productMapper.toProduct(request, auth));
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setUrl(request.url());
        product.setCategory(Category.builder().id(request.category().id()).build());
        super.update(product);
        return productMapper.toResponse(product);
    }

    @Override
    public void delete(ProductRequest request, String auth) throws ServiceException {
        log.trace("");

        Product product = findProduct(productMapper.toProduct(request, auth));
        Inventory inventory = Inventory.builder().user(product.getUser()).product(product).build();
        if (businessService.hasInventory(inventory, auth)) {
            throw new ServiceException(HttpServletResponse.SC_CONFLICT, "Product has inventory.");
        }
        super.delete(product);
    }

    @Override
    public BigDecimal calculateTotalPriceFor(Product request) {
        ProductDAO DAO = this.getDAO();
        return DAO.calculateTotalPriceFor(request);
    }

    @Override
    @SneakyThrows
    public Optional<List<ProductResponse>> scrape(String url, String environment, String auth) {
        log.trace("");

        if (!"development".equals(environment)) {
            log.warn("Web scraping is only allowed in development environment");
            return Optional.empty();
        }

        final User user = getUser(auth);

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
                .map(productMapper::scrapeToProduct)
                .map(product -> prepareProductToSave(product, user))
                .toList();
        try {
            products = baseDAO.save(products);
            return Optional.of(products.stream().map(productMapper::toResponse).toList());

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

    private Product findProduct(Product product) throws ServiceException {
        return this.find(product).orElseThrow(() -> notFound("Product not found"));
    }
}
