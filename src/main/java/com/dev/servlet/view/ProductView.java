package com.dev.servlet.view;

import com.dev.servlet.controllers.ProductController;
import com.dev.servlet.domain.Category;
import com.dev.servlet.domain.Inventory;
import com.dev.servlet.domain.Product;
import com.dev.servlet.domain.enums.StatusEnum;
import com.dev.servlet.dto.CategoryDto;
import com.dev.servlet.dto.ProductDto;
import com.dev.servlet.filter.StandardRequest;
import com.dev.servlet.interfaces.Inject;
import com.dev.servlet.interfaces.ResourcePath;
import com.dev.servlet.mapper.CategoryMapper;
import com.dev.servlet.mapper.ProductMapper;
import com.dev.servlet.utils.CurrencyFormatter;
import com.dev.servlet.view.base.BaseRequest;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProductView extends BaseRequest {

    private static final String FORWARD_PAGE_LIST = "forward:pages/product/formListProduct.jsp";
    private static final String FORWARD_PAGE_LIST_PRODUCTS = "forward:pages/product/listProducts.jsp";
    private static final String FORWARD_PAGE_UPDATE = "forward:pages/product/formUpdateProduct.jsp";
    private static final String FORWARD_PAGE_CREATE = "forward:pages/product/formCreateProduct.jsp";

    private static final String REDIRECT_ACTION_LIST_ALL = "redirect:productView?action=list";
    private static final String REDIRECT_ACTION_LIST_BY_ID = "redirect:productView?action=list&id=";

    private static final String CACHE_KEY = "categories";

    private ProductController controller;
    @Inject
    private CategoryView categoryView;
    @Inject
    private InventoryView inventoryView;

    public ProductView() {
    }

    public ProductView(EntityManager entityManager) {
        this.controller = new ProductController(entityManager);
    }

    /**
     * Forward
     *
     * @return the next path
     */
    @ResourcePath(value = NEW)
    public String forwardRegister(StandardRequest standardRequest) {
        HttpServletRequest request = standardRequest.getRequest();
        List<CategoryDto> categories = categoryView.findAll(request);
        request.setAttribute("categories", categories);
        return FORWARD_PAGE_CREATE;
    }

    /**
     * Create one
     *
     * @param standardRequest
     * @return the next path
     */
    @ResourcePath(value = CREATE)
    public String register(StandardRequest standardRequest) {
        HttpServletRequest request = standardRequest.getRequest();

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
        product.setStatus(StatusEnum.ACTIVE.getName());
        controller.save(product);
        request.setAttribute("product", product);

        return REDIRECT_ACTION_LIST_BY_ID + product.getId();
    }

    /**
     * Forward edit
     *
     * @param standardRequest
     * @return the next path
     */
    @ResourcePath(value = EDIT)
    public String edit(StandardRequest standardRequest) {
        HttpServletRequest request = standardRequest.getRequest();

        String id = getParameter(request, "id");
        if (id == null) {
            request.setAttribute("error", "id can't be null");
            return FORWARD_PAGES_NOT_FOUND;
        }

        Product product = new Product();
        product = controller.findById(Long.valueOf(id));

        request.setAttribute("product", ProductMapper.from(product));
        request.setAttribute("categories", categoryView.findAll(request));

        return FORWARD_PAGE_UPDATE;
    }

    /**
     * List one or many
     *
     * @param standardRequest
     * @return the next path
     */
    @ResourcePath(value = LIST)
    public String list(StandardRequest standardRequest) {
        HttpServletRequest request = standardRequest.getRequest();
        String id = getParameter(request, "id");
        if (id != null) {
            Product product = controller.findById(Long.valueOf(id));
            if (product == null) {
                return FORWARD_PAGES_NOT_FOUND;
            }
            request.setAttribute("product", ProductMapper.from(product));
            return FORWARD_PAGE_LIST;
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

        List<ProductDto> products = findAll(product);
        request.setAttribute("products", products);

        String categoryId = getParameter(request, "categoryId");
        if (categoryId != null) {
            List<Category> categories = categoryView.findAll(request).stream().map(CategoryMapper::from).toList();
            request.setAttribute("categories", categories);
        }

        return FORWARD_PAGE_LIST_PRODUCTS;
    }

    /**
     * Update one
     *
     * @param standardRequest
     * @return the next path
     */
    @ResourcePath(value = UPDATE)
    public String update(StandardRequest standardRequest) {
        HttpServletRequest request = standardRequest.getRequest();

        Product product = controller.findById(Long.parseLong(getParameter(request, "id")));
        product.setName(getParameter(request, "name"));
        product.setDescription(getParameter(request, "description"));
        product.setPrice(CurrencyFormatter.stringToBigDecimal(getParameter(request, "price")));
        product.setUrl(getParameter(request, "url"));
        product.setCategory(new Category(Long.parseLong(getParameter(request, "category"))));

        controller.update(product);
        request.setAttribute("product", ProductMapper.from(product));

        return REDIRECT_ACTION_LIST_BY_ID + product.getId();
    }

    /**
     * Delete one
     *
     * @param standardRequest
     * @return the next path
     */
    @ResourcePath(value = DELETE)
    public String delete(StandardRequest standardRequest) {
        HttpServletRequest req = standardRequest.getRequest();
        Long id = Long.parseLong(getParameter(req, "id"));
        Product product = new Product(id);
        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        if (inventoryView.hasInventory(inventory)) {
            req.setAttribute("error", "Product has inventory");
            return FORWARD_PAGES_NOT_FOUND;
        }

        controller.delete(product);
        return REDIRECT_ACTION_LIST_ALL;
    }

    /**
     * Find All
     *
     * @param request
     * @return the next path
     */
    public List<ProductDto> findAll(Product product) {
        List<Product> products = controller.findAll(product);
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
