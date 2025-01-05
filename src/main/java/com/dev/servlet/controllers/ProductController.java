package com.dev.servlet.controllers;

import com.dev.servlet.dto.CategoryDTO;
import com.dev.servlet.dto.ProductDTO;
import com.dev.servlet.dto.ServiceException;
import com.dev.servlet.interfaces.Controller;
import com.dev.servlet.interfaces.IHttpResponse;
import com.dev.servlet.interfaces.IServletResponse;
import com.dev.servlet.interfaces.RequestMapping;
import com.dev.servlet.mapper.ProductMapper;
import com.dev.servlet.model.CategoryModel;
import com.dev.servlet.model.ProductModel;
import com.dev.servlet.pojo.Product;
import com.dev.servlet.pojo.records.HttpResponse;
import com.dev.servlet.pojo.records.KeyPair;
import com.dev.servlet.pojo.records.Pagination;
import com.dev.servlet.pojo.records.Request;
import com.dev.servlet.utils.CollectionUtils;
import lombok.NoArgsConstructor;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Controller(path = "/product")
public final class ProductController extends BaseController<Product, Long> {

    @Inject
    private CategoryModel categoryModel;

    @Inject
    public ProductController(ProductModel model) {
        super(model);
    }

    private ProductModel getModel() {
        return (ProductModel) super.getBaseModel();
    }

    /**
     * Create a new product
     *
     * @param request {@linkplain Request}
     * @return {@linkplain IHttpResponse}
     */
    @RequestMapping(value = CREATE, method = "POST")
    public IHttpResponse<Void> create(Request request) {
        ProductDTO product = this.getModel().create(request);
        // Created
        return super.newHttpResponse(201, null, super.redirectTo(product.getId()));
    }

    /**
     * Load data and forward to the create product form
     *
     * @param request {@linkplain Request}
     * @return {@linkplain IHttpResponse}
     */
    @RequestMapping(value = NEW, method = "GET")
    public IHttpResponse<Collection<CategoryDTO>> forward(Request request) {
        var categories = categoryModel.getAllFromCache(request.getToken());
        // Found
        return super.newHttpResponse(302, categories, super.forwardTo("formCreateProduct"));
    }

    /**
     * Load data and forward to the edit product form
     *
     * @param request {@linkplain Request}
     * @return {@linkplain IServletResponse}
     * @throws ServiceException if any error occurs
     */
    @RequestMapping(value = EDIT, method = "GET")
    public IServletResponse edit(Request request) throws ServiceException {
        ProductDTO product = this.getModel().getById(request);
        Collection<CategoryDTO> categories = categoryModel.getAllFromCache(request.getToken());

        Set<KeyPair> data = Set.of(
                new KeyPair("product", product),
                new KeyPair("categories", categories)
        );

        return super.newServletResponse(data, super.forwardTo("formUpdateProduct"));
    }

    /**
     * List all products
     *
     * @param request {@linkplain Request} with query
     * @return {@linkplain IServletResponse} with {@linkplain ProductDTO}
     */
    @RequestMapping(value = LIST, method = "GET")
    public IServletResponse list(Request request) {
        ProductModel model = this.getModel();

        Collection<Long> productsIds = model.findAll(request);
        Pagination pagination = request.getQuery().getPagination();
        pagination.setTotalRecords(productsIds.size());

        Set<KeyPair> response = new HashSet<>();
        if (!CollectionUtils.isEmpty(productsIds)) {
            Collection<Product> products = model.getAllPageable(productsIds, pagination);
            Collection<ProductDTO> productDTOs = products.stream().map(ProductMapper::base).toList();

            BigDecimal totalPrice = model.calculateTotalPrice(productsIds);

            response.add(new KeyPair("products", productDTOs));
            response.add(new KeyPair("totalPrice", totalPrice));
        }

        Collection<CategoryDTO> categories = categoryModel.getAllFromCache(request.getToken());
        response.add(new KeyPair("categories", categories));

        return super.newServletResponse(response, super.forwardTo("listProducts"));


    }

    /**
     * List product by id
     *
     * @param request {@linkplain Request}
     * @return {@linkplain IHttpResponse} with {@linkplain ProductDTO}
     * @throws ServiceException if any error occurs
     */
    @RequestMapping(value = "/{id}", method = "GET")
    public IHttpResponse<ProductDTO> listById(Request request) throws ServiceException {
        ProductDTO product = this.getModel().getById(request);
        // OK
        return super.newHttpResponse(200, product, super.forwardTo("formListProduct"));
    }

    /**
     * Update a product
     *
     * @param request {@linkplain Request}
     * @return {@linkplain IHttpResponse}
     * @throws ServiceException if any error occurs
     */
    @RequestMapping(value = UPDATE, method = "POST")
    public IHttpResponse<Void> update(Request request) throws ServiceException {
        ProductDTO product = this.getModel().update(request);
        // No Content
        return super.newHttpResponse(204, null, super.redirectTo(product.getId()));
    }

    /**
     * Delete a product
     *
     * @param request {@linkplain Request}
     * @return {@linkplain IHttpResponse} with no content {@linkplain Void}
     * @throws ServiceException if any error occurs
     */
    @RequestMapping(value = DELETE, method = "POST")
    public IHttpResponse<Void> delete(Request request) throws ServiceException {
        this.getModel().delete(request);

        return HttpResponse.ofNext(super.redirectTo(LIST));
    }
}
