package com.dev.servlet.business;

import com.dev.servlet.business.base.BaseRequest;
import com.dev.servlet.controllers.ProductController;
import com.dev.servlet.dto.CategoryDto;
import com.dev.servlet.dto.ProductDto;
import com.dev.servlet.interfaces.IService;
import com.dev.servlet.interfaces.ResourcePath;
import com.dev.servlet.mapper.ProductMapper;
import com.dev.servlet.pojo.Category;
import com.dev.servlet.pojo.Inventory;
import com.dev.servlet.pojo.Product;
import com.dev.servlet.pojo.enums.StatusEnum;
import com.dev.servlet.pojo.records.Pagable;
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
        List<CategoryDto> categories = categoryBusiness.findAll(request);
        request.servletRequest().setAttribute(CATEGORIES, categories);
        return FORWARD_PAGES_PRODUCT + "formCreateProduct.jsp";
    }

    /**
     * Create one
     *
     * @param request
     * @return the next path
     */
    @ResourcePath(CREATE)
    public String register(StandardRequest request) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate = LocalDate.parse(LocalDate.now().format(formatter), formatter);

        Product product = new Product(
                getParameter(request, "name"),
                getParameter(request, "description"),
                getParameter(request, "url"),
                localDate,
                CurrencyFormatter.stringToBigDecimal(getParameter(request, "price")));

        product.setUser(getUser(request));
        product.setCategory(new Category(Long.valueOf(getParameter(request, "category"))));
        product.setStatus(StatusEnum.ACTIVE.value);
        controller.save(product);
        request.servletRequest().setAttribute(PRODUCT, product);
        request.servletResponse().setStatus(HttpServletResponse.SC_CREATED);

        return REDIRECT_ACTION_LIST_BY_ID.replace("<id>", product.getId().toString());
    }

    /**
     * Forward edit
     *
     * @param request
     * @return the next path
     */
    @ResourcePath(EDIT)
    public String edit(StandardRequest request) throws IOException {
        Long resourceId = request.requestObject().resourceId();
        if (resourceId == null) {
            request.servletResponse().sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        Product product = new Product(resourceId);
        product.setUser(getUser(request));
        product = controller.find(product);
        if (product == null) {
            request.servletResponse().sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        request.servletRequest().setAttribute(PRODUCT, ProductMapper.from(product));
        request.servletRequest().setAttribute(CATEGORIES, categoryBusiness.findAll(request));

        return FORWARD_PAGES_PRODUCT + "formUpdateProduct.jsp";
    }

    /**
     * List one or many (with pagination)
     *
     * @param request
     * @return the next path
     */
    @ResourcePath(LIST)
    public String getAll(StandardRequest request) throws Exception {
        Long resourceId = request.requestObject().resourceId();
        if (resourceId != null) {
            Product product = new Product(resourceId);
            product.setUser(getUser(request));
            ProductDto dto = find(product);
            if (dto == null) {
                request.servletResponse().sendError(HttpServletResponse.SC_BAD_REQUEST);
                return null;
            }

            request.servletRequest().setAttribute(PRODUCT, dto);
            return FORWARD_PAGES_PRODUCT + "formListProduct.jsp";
        }

        Product product = new Product();
        product.setUser(getUser(request));

        String param = getParameter(request, PARAM);
        String value = getParameter(request, VALUE);
        if (param != null && value != null) {
            if (param.equals("name")) {
                product.setName(value);
            } else {
                product.setDescription(value);
            }
        }

        request.pagination().setTotalRecords(controller.getTotalResults(product).intValue());

        List<ProductDto> products = findAll(product, request.pagination());

        request.servletRequest().setAttribute("products", products);

        String categoryId = getParameter(request, "categoryId");
        if (categoryId != null) {
            CategoryDto categoryDto = categoryBusiness.findById(Long.parseLong(categoryId), request);
            request.servletRequest().setAttribute("category", categoryDto);
        } else {
            List<CategoryDto> categoriesDto = categoryBusiness.findAll(request);
            request.servletRequest().setAttribute(CATEGORIES, categoriesDto);
        }

        request.servletResponse().setStatus(HttpServletResponse.SC_OK);
        return FORWARD_PAGES_PRODUCT + "listProducts.jsp";
    }

    /**
     * Update one
     *
     * @param request
     * @return the next path
     */
    @ResourcePath(UPDATE)
    public String update(StandardRequest request) throws IOException {
        Product product = new Product(request.requestObject().resourceId());
        product.setUser(getUser(request));
        product = controller.find(product);
        if (product == null) {
            request.servletResponse().sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        product = controller.find(product);
        product.setName(getParameter(request, "name"));
        product.setDescription(getParameter(request, "description"));
        product.setPrice(CurrencyFormatter.stringToBigDecimal(getParameter(request, "price")));
        product.setUrl(getParameter(request, "url"));
        product.setCategory(new Category(Long.parseLong(getParameter(request, "category"))));

        product = controller.update(product);
        request.servletRequest().setAttribute(PRODUCT, ProductMapper.from(product));
        request.servletResponse().setStatus(HttpServletResponse.SC_NO_CONTENT);
        return REDIRECT_ACTION_LIST_BY_ID.replace("<id>", product.getId().toString());
    }

    /**
     * Delete one
     *
     * @param request
     * @return the next path
     */
    @ResourcePath(DELETE)
    public String delete(StandardRequest request) throws Exception {
        Product product = new Product(request.requestObject().resourceId());
        product.setUser(getUser(request));

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        if (productShared.hasInventory(inventory)) {
            request.servletResponse().sendError(HttpServletResponse.SC_CONFLICT, "Product has inventory");
            return null;
        }

        controller.delete(product);
        request.servletResponse().setStatus(HttpServletResponse.SC_NO_CONTENT);
        return REDIRECT_VIEW_PRODUCT + "list";
    }

    /**
     * Find All
     *
     * @param product
     * @return the next path
     */
    public List<ProductDto> findAll(Product product, Pagable pagable) {
        List<Product> products = (List<Product>) controller.findAll(product, pagable);
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
