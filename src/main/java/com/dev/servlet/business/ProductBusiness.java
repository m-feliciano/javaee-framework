package com.dev.servlet.business;

import com.dev.servlet.business.base.BaseRequest;
import com.dev.servlet.controllers.ProductController;
import com.dev.servlet.dto.CategoryDto;
import com.dev.servlet.dto.ProductDto;
import com.dev.servlet.dto.ServiceException;
import com.dev.servlet.interfaces.IService;
import com.dev.servlet.interfaces.ResourcePath;
import com.dev.servlet.mapper.ProductMapper;
import com.dev.servlet.pojo.Category;
import com.dev.servlet.pojo.Inventory;
import com.dev.servlet.pojo.Product;
import com.dev.servlet.pojo.enums.StatusEnum;
import com.dev.servlet.pojo.records.Pagination;
import com.dev.servlet.pojo.records.Query;
import com.dev.servlet.pojo.records.StandardRequest;
import com.dev.servlet.utils.CurrencyFormatter;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Product Business
 * <p>
 * This class is responsible for handling the product business logic.
 *
 * @see BaseRequest
 */
@Singleton
@IService("product")
public class ProductBusiness extends BaseRequest {
    public static final String PRODUCT = "product";
    public static final String CATEGORIES = "categories";
    public static final String FORWARD_PAGES_PRODUCT = "forward:pages/product/";
    public static final String REDIRECT_VIEW_PRODUCT = "redirect:/view/product/";
    private static final String REDIRECT_ACTION_LIST_BY_ID = REDIRECT_VIEW_PRODUCT + "list/<id>";

    private ProductController controller;
    private CategoryBusiness categoryBusiness;
    private ProductShared productShared;

    public ProductBusiness() {
        // Empty constructor
    }

    @Inject
    public void setDependencies(ProductController controller,
                                CategoryBusiness categoryBusiness,
                                ProductShared productShared) {
        this.controller = controller;
        this.categoryBusiness = categoryBusiness;
        this.productShared = productShared;
    }

    /**
     * Forward
     *
     * @return the next path
     */
    @ResourcePath(NEW)
    public String forwardRegister(StandardRequest request) {
        List<CategoryDto> categories = categoryBusiness.getAllFromCache(request);
        request.setAttribute(CATEGORIES, categories);
        return FORWARD_PAGES_PRODUCT + "formCreateProduct.jsp";
    }

    /**
     * Create one
     *
     * @param request
     * @return the next path
     */
    @ResourcePath(CREATE)
    public String register(StandardRequest request) throws ServiceException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate = LocalDate.parse(LocalDate.now().format(formatter), formatter);

        Product product = new Product(
                request.getParameter("name"),
                request.getParameter("description"),
                request.getParameter("url"),
                localDate,
                CurrencyFormatter.stringToBigDecimal(request.getRequiredParameter("price")));

        product.setUser(getUser(request));
        product.setCategory(new Category(Long.valueOf(request.getRequiredParameter("category"))));
        product.setStatus(StatusEnum.ACTIVE.value);
        controller.save(product);
        request.setAttribute(PRODUCT, product);
        request.setStatus(HttpServletResponse.SC_CREATED);

        return REDIRECT_ACTION_LIST_BY_ID.replace("<id>", product.getId().toString());
    }

    /**
     * Forward edit
     *
     * @param request
     * @return the next path
     */
    @ResourcePath(EDIT)
    public String edit(StandardRequest request) throws IOException, ServiceException {
        Long resourceId = request.getId();
        if (resourceId == null) throwResourceNotFoundException(null);

        Product product = new Product(resourceId);
        product.setUser(getUser(request));
        product = controller.find(product);
        if (product == null) throwResourceNotFoundException(resourceId);

        request.setAttribute(PRODUCT, ProductMapper.from(product));
        request.setAttribute(CATEGORIES, categoryBusiness.getAllFromCache(request));

        return FORWARD_PAGES_PRODUCT + "formUpdateProduct.jsp";
    }

    /**
     * List one or many (with query)
     *
     * @param request
     * @return the next path
     */
    @ResourcePath(LIST)
    public String getAll(StandardRequest request) throws Exception, ServiceException {
        if (request.getId() != null) {
            Product product = new Product(request.getId());
            product.setUser(getUser(request));
            ProductDto dto = find(product);

            if (dto == null) throwResourceNotFoundException(request.getId());

            request.setAttribute(PRODUCT, dto);
            return FORWARD_PAGES_PRODUCT + "formListProduct.jsp";
        }

        Product product = new Product();
        product.setUser(getUser(request));

        Query query = request.getQuery();
        if (query.search() != null && query.type() != null) {
            if (query.type().equals("name")) {
                product.setName(query.search());
            } else if (query.type().equals("description")) {
                product.setDescription(query.search());
            }
        }

        List<ProductDto> products = findAll(product, query.pagination());

        query.pagination().setTotalRecords(controller.getTotalResults(product).intValue());

        request.setAttribute("products", products);

        String categoryId = request.getParameter("categoryId");
        if (categoryId != null) {
            CategoryDto categoryDto = categoryBusiness.findById(Long.parseLong(categoryId), request);
            request.setAttribute("category", categoryDto);
        } else {
            List<CategoryDto> categoriesDto = categoryBusiness.getAllFromCache(request);
            request.setAttribute(CATEGORIES, categoriesDto);
        }

        request.setStatus(HttpServletResponse.SC_OK);
        return FORWARD_PAGES_PRODUCT + "listProducts.jsp";
    }

    /**
     * Update one
     *
     * @param request
     * @return the next path
     */
    @ResourcePath(UPDATE)
    public String update(StandardRequest request) throws ServiceException {
        if (request.getId() == null) throwResourceNotFoundException(null);

        Product product = new Product(request.getId());
        product.setUser(getUser(request));
        product = controller.find(product);

        if (product == null) throwResourceNotFoundException(request.getId());

        product = controller.find(product);
        product.setName(request.getParameter("name"));
        product.setDescription(request.getParameter("description"));
        product.setPrice(CurrencyFormatter.stringToBigDecimal(request.getRequiredParameter("price")));
        product.setUrl(request.getParameter("url"));
        product.setCategory(new Category(Long.parseLong(request.getRequiredParameter("category"))));

        product = controller.update(product);
        request.setAttribute(PRODUCT, ProductMapper.from(product));
        request.setStatus(HttpServletResponse.SC_NO_CONTENT);
        return REDIRECT_ACTION_LIST_BY_ID.replace("<id>", product.getId().toString());
    }

    /**
     * Delete one
     *
     * @param request
     * @return the next path
     */
    @ResourcePath(DELETE)
    public String delete(StandardRequest request) throws ServiceException {
        if (request.getId() == null) throwResourceNotFoundException(null);

        Product product = new Product(request.getId());
        product.setUser(getUser(request));

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        if (productShared.hasInventory(inventory)) {
            throw new ServiceException(HttpServletResponse.SC_CONFLICT, "Product has inventory.");
        }

        controller.delete(product);
        request.setStatus(HttpServletResponse.SC_NO_CONTENT);
        return REDIRECT_VIEW_PRODUCT + "list";
    }

    /**
     * Find All
     *
     * @param product
     * @param pagination
     * @return the next path
     */
    public List<ProductDto> findAll(Product product, Pagination pagination) {
        List<Product> products = (List<Product>) controller.findAll(product, pagination);
        return products.stream().map(ProductMapper::from).toList();
    }

    /**
     * Find by id
     *
     * @param product
     * @return the next path
     */
    public ProductDto find(Product product) {
        Product p = controller.find(product);
        return ProductMapper.from(p);
    }
}
