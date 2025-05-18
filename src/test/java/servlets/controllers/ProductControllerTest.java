package servlets.controllers;

import com.dev.servlet.controllers.CategoryController;
import com.dev.servlet.controllers.ProductController;
import com.dev.servlet.dto.CategoryDTO;
import com.dev.servlet.dto.ProductDTO;
import com.dev.servlet.dto.ServiceException;
import com.dev.servlet.interfaces.IHttpResponse;
import com.dev.servlet.interfaces.IServletResponse;
import com.dev.servlet.model.CategoryModel;
import com.dev.servlet.model.ProductModel;
import com.dev.servlet.pojo.Pageable;
import com.dev.servlet.pojo.domain.Product;
import com.dev.servlet.pojo.records.Query;
import com.dev.servlet.pojo.records.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductControllerTest {

    @Mock
    private ProductModel productModel;

    @Mock
    private CategoryController categoryController;

    @Mock
    private CategoryModel categoryModel;

    @InjectMocks
    private ProductController productController;

    @Mock
    private Request request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productController.setCategoryController(categoryController);

        when(categoryController.getBaseModel()).thenReturn(categoryModel);
        when(request.token()).thenReturn("fakeToken");
        when(request.query()).thenReturn(mock(Query.class));
        when(request.query().getPageable()).thenReturn(mock(Pageable.class));
        when(request.query().getPageable().getRecords()).thenReturn(List.of(1L, 2L));
    }

    @Test
    @DisplayName(
            "Test create method to add a new product. " +
            "It should return a 201 status code and the expected response.")
    void testCreateProduct() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1L);

        when(productModel.create(request)).thenReturn(productDTO);

        IHttpResponse<Void> response = productController.create(request);

        assertNotNull(response);
        assertEquals(201, response.statusCode());
        verify(productModel, times(1)).create(request);
    }

    @Test
    @DisplayName(
            "Test forward method to create form. " +
            "It should return a 302 status code and the expected response.")
    void testForwardToCreateForm() {
        Collection<CategoryDTO> categories = List.of(new CategoryDTO());

        when(categoryModel.getAllFromCache(anyString())).thenReturn(categories);

        IHttpResponse<Collection<CategoryDTO>> response = productController.forward(request);

        assertNotNull(response);
        assertEquals(302, response.statusCode());
        assertEquals(categories, response.body());
        verify(categoryModel, times(1)).getAllFromCache(anyString());
    }

    @Test
    @DisplayName(
            "Test edit method to update a product. " +
            "It should return a 200 status code and the expected response.")
    void testEditProduct() throws ServiceException {
        ProductDTO productDTO = new ProductDTO();
        Collection<CategoryDTO> categories = List.of(new CategoryDTO());

        when(productModel.getById(request)).thenReturn(productDTO);
        when(categoryModel.getAllFromCache(anyString())).thenReturn(categories);

        IServletResponse response = productController.edit(request);

        assertNotNull(response);
        verify(productModel, times(1)).getById(request);
        verify(categoryModel, times(1)).getAllFromCache(anyString());
    }

    @Test
    @DisplayName(
            "Test update method to save changes to a product. " +
            "It should return a 200 status code and the expected response.")
    void testListProducts() {
        Collection<Long> productIds = List.of(1L, 2L);
        Collection<Product> products = List.of(new Product(), new Product());
        BigDecimal totalPrice = BigDecimal.valueOf(100);

        when(categoryModel.getAllFromCache(any())).thenReturn(List.of(new CategoryDTO()));
        when(productModel.findAll(request)).thenReturn(productIds);
        when(productModel.getAllPageable(any())).thenReturn(products);
        when(productModel.calculateTotalPrice(productIds)).thenReturn(totalPrice);

        IServletResponse response = productController.list(request);

        assertNotNull(response);
        assertEquals(200, response.statusCode());

        verify(productModel, times(1)).findAll(request);
        verify(productModel, times(1)).getAllPageable(any());
        verify(productModel, times(1)).calculateTotalPrice(productIds);
        verify(categoryModel, times(1)).getAllFromCache(anyString());
    }

    @Test
    @DisplayName(
            "Test delete method to remove a product. " +
            "It should return a 200 status code and the expected response.")
    void testDeleteProduct() throws ServiceException {
        doNothing().when(productModel).delete(request);

        IHttpResponse<Void> response = productController.delete(request);

        assertNotNull(response);
        assertEquals(200, response.statusCode());
        verify(productModel, times(1)).delete(request);
    }

    @Test
    @DisplayName(
            "Test listById method to retrieve a product by ID. " +
            "It should return a 200 status code and the expected response.")
    void testListById() throws ServiceException {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1L);

        when(productModel.getById(request)).thenReturn(productDTO);

        IHttpResponse<ProductDTO> response = productController.listById(request);
        assertNotNull(response);
        assertEquals(200, response.statusCode());
        assertEquals(productDTO, response.body());
        verify(productModel, times(1)).getById(request);
    }
}