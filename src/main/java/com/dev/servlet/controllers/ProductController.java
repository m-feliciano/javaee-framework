package com.dev.servlet.controllers;

import com.dev.servlet.dto.CategoryDTO;
import com.dev.servlet.dto.ProductDTO;
import com.dev.servlet.dto.ServiceException;
import com.dev.servlet.interfaces.Constraints;
import com.dev.servlet.interfaces.Controller;
import com.dev.servlet.interfaces.IHttpResponse;
import com.dev.servlet.interfaces.IServletResponse;
import com.dev.servlet.interfaces.RequestMapping;
import com.dev.servlet.interfaces.Validator;
import com.dev.servlet.mapper.ProductMapper;
import com.dev.servlet.model.CategoryModel;
import com.dev.servlet.model.ProductModel;
import com.dev.servlet.pojo.Pagination;
import com.dev.servlet.pojo.domain.Product;
import com.dev.servlet.pojo.enums.RequestMethod;
import com.dev.servlet.pojo.records.HttpResponse;
import com.dev.servlet.pojo.records.KeyPair;
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

    private CategoryController categoryController;

    @Inject
    public ProductController(ProductModel productModel) {
        super(productModel);
    }

    @Inject
    public void setCategoryController(CategoryController categoryController) {
        this.categoryController = categoryController;
    }

    private ProductModel getModel() {
        return (ProductModel) super.getBaseModel();
    }

    private CategoryModel getCategoryModel() {
        return (CategoryModel) categoryController.getBaseModel();
    }

    /**
     * Create a new product
     *
     * @param request {@linkplain Request}
     * @return {@linkplain IHttpResponse}
     */
    @RequestMapping(
            value = "/create",
            method = RequestMethod.POST,
            validators = {
                    @Validator(values = "name", constraints = {
                            @Constraints(minLength = 3, maxLength = 50, message = "Name must be between {0} and {1} characters")
                    }),
                    @Validator(values = "description", constraints = {
                            @Constraints(minLength = 5, maxLength = 255, message = "Description must be between {0} and {1} characters")
                    }),
                    @Validator(values = "price", constraints = {
                            @Constraints(min = 0, message = "Price must be greater than or equal to {0}")
                    })
            })
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
    @RequestMapping(value = "/new")
    public IHttpResponse<Collection<CategoryDTO>> forward(Request request) {
        var categories = getCategoryModel().getAllFromCache(request.token());
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
    @RequestMapping(
            value = "/edit/{id}",
            validators = {
                    @Validator(values = "id", constraints = {
                            @Constraints(min = 1, message = "ID must be greater than or equal to {0}")
                    })
            })
    public IServletResponse edit(Request request) throws ServiceException {
        ProductDTO product = this.getModel().getById(request);
        Collection<CategoryDTO> categories = getCategoryModel().getAllFromCache(request.token());

        Set<KeyPair> data = Set.of(
                KeyPair.of("product", product),
                KeyPair.of("categories", categories)
        );

        return super.newServletResponse(data, super.forwardTo("formUpdateProduct"));
    }

    /**
     * List all products
     *
     * @param request {@linkplain Request} with query
     * @return {@linkplain IServletResponse} with {@linkplain ProductDTO}
     */
    @RequestMapping(value = "/list")
    public IServletResponse list(Request request) {
        ProductModel model = this.getModel();

        Collection<Long> productsIds = model.findAll(request);
        Pagination pagination = request.query().getPagination();
        pagination.setTotalRecords(productsIds.size());

        Set<KeyPair> response = new HashSet<>();
        if (!CollectionUtils.isEmpty(productsIds)) {
            Collection<Product> products = model.getAllPageable(productsIds, pagination);
            Collection<ProductDTO> productDTOs = products.stream().map(ProductMapper::base).toList();

            BigDecimal totalPrice = model.calculateTotalPrice(productsIds);

            response.add(KeyPair.of("products", productDTOs));
            response.add(KeyPair.of("totalPrice", totalPrice));
        }

        Collection<CategoryDTO> categories = getCategoryModel().getAllFromCache(request.token());
        response.add(KeyPair.of("categories", categories));

        return super.newServletResponse(response, super.forwardTo("listProducts"));
    }

    /**
     * List product by id
     *
     * @param request {@linkplain Request}
     * @return {@linkplain IHttpResponse} with {@linkplain ProductDTO}
     * @throws ServiceException if any error occurs
     */
    @RequestMapping(
            value = "/list/{id}",
            validators = {
                    @Validator(values = "id", constraints = {
                            @Constraints(min = 1, message = "ID must be greater than or equal to {0}")
                    })
            })
    public IHttpResponse<ProductDTO> listById(Request request) throws ServiceException {
        ProductDTO product = this.getModel().getById(request);
        // OK
        return super.okHttpResponse(product, super.forwardTo("formListProduct"));
    }

    /**
     * Update a product
     *
     * @param request {@linkplain Request}
     * @return {@linkplain IHttpResponse}
     * @throws ServiceException if any error occurs
     */
    @RequestMapping(
            value = "/update/{id}",
            method = RequestMethod.POST,
            validators = {
                    @Validator(values = "id", constraints = {
                            @Constraints(min = 1, message = "ID must be greater than or equal to {0}")
                    }),
                    @Validator(values = "name", constraints = {
                            @Constraints(minLength = 3, maxLength = 50, message = "Name must be between {0} and {1} characters")
                    }),
                    @Validator(values = "description", constraints = {
                            @Constraints(minLength = 5, maxLength = 255, message = "Description must be between {0} and {1} characters")
                    }),
                    @Validator(values = "price", constraints = {
                            @Constraints(min = 0, message = "Price must be greater than or equal to {0}")
                    })
            })
    public IHttpResponse<Void> update(Request request) throws ServiceException {
        ProductDTO product = this.getModel().update(request);
        // No Content
        String nextPath = super.redirectTo(product.getId());
        return super.newHttpResponse(204, null, nextPath);
    }

    /**
     * Delete a product
     *
     * @param request {@linkplain Request}
     * @return {@linkplain IHttpResponse} with no content {@linkplain Void}
     * @throws ServiceException if any error occurs
     */
    @RequestMapping(
            value = "/delete/{id}",
            method = RequestMethod.POST,
            validators = {
                    @Validator(values = "id", constraints = {
                            @Constraints(
                                    min = 1,
                                    message = "ID must be greater than or equal to {0}"
                            )
                    })
            })
    public IHttpResponse<Void> delete(Request request) throws ServiceException {
        this.getModel().delete(request);

        return HttpResponse.ofNext(super.redirectTo(LIST));
    }
}
