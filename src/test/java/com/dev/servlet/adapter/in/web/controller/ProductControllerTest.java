package com.dev.servlet.adapter.in.web.controller;

import com.dev.servlet.adapter.in.web.controller.internal.ProductController;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.adapter.in.web.dto.IServletResponse;
import com.dev.servlet.application.mapper.ProductMapper;
import com.dev.servlet.application.port.in.category.ListCategoryPort;
import com.dev.servlet.application.port.in.product.CreateProductWithThumbPort;
import com.dev.servlet.application.port.in.product.DeleteProductPort;
import com.dev.servlet.application.port.in.product.ListProductContainerPort;
import com.dev.servlet.application.port.in.product.ProductDetailPort;
import com.dev.servlet.application.port.in.product.ScrapeProductPort;
import com.dev.servlet.application.port.in.product.UpdateProductPort;
import com.dev.servlet.application.port.in.product.UpdateProductThumbPort;
import com.dev.servlet.application.transfer.request.FileUploadRequest;
import com.dev.servlet.application.transfer.request.ProductRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import com.dev.servlet.application.transfer.response.ProductResponse;
import com.dev.servlet.domain.entity.Credentials;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.vo.BinaryPayload;
import com.dev.servlet.infrastructure.persistence.transfer.internal.PageRequest;
import com.dev.servlet.shared.vo.KeyPair;
import com.dev.servlet.shared.vo.Query;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("ProductController Tests")
class ProductControllerTest extends BaseControllerTest {

    @Mock
    private ProductDetailPort productDetailPort;
    @Mock
    private DeleteProductPort deleteProductPort;
    @Mock
    private UpdateProductPort updateProductPort;
    @Mock
    private CreateProductWithThumbPort createProductWithThumbPort;
    @Mock
    private ScrapeProductPort scrapeProductPort;
    @Mock
    private ListCategoryPort listCategoryPort;
    @Mock
    private ListProductContainerPort listProductContainerPort;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private UpdateProductThumbPort updateProductThumbPort;

    @InjectMocks
    private ProductController productController;

    @Override
    protected void setupAdditionalMocks() {
        productController.setJwtUtils(authenticationPort);

        User mockUser = User.builder().id(USER_ID).credentials(new Credentials()).build();

        // Setup default mocks
        lenient().when(authenticationPort.extractUser(VALID_AUTH_TOKEN)).thenReturn(mockUser);
        lenient().when(authenticationPort.extractUserId(VALID_AUTH_TOKEN)).thenReturn(USER_ID);

        ProductResponse mockProduct = new ProductResponse("product-123");
        mockProduct.setName("Test Product");
        mockProduct.setPrice(new BigDecimal("99.99"));

        lenient().when(createProductWithThumbPort.execute(any(), any())).thenReturn(mockProduct);
        lenient().when(updateProductPort.update(any(), any())).thenReturn(mockProduct);
        lenient().when(productDetailPort.get(any(), any())).thenReturn(mockProduct);
        lenient().when(listCategoryPort.list(any(), any())).thenReturn(List.of());
        lenient().when(productMapper.toProduct(any(), any())).thenReturn(Product.builder().build());
        lenient().when(productMapper.queryToProduct(any(), any())).thenReturn(Product.builder().build());
        lenient().when(listProductContainerPort.assembleContainerResponse(any(), any(), any()))
                .thenReturn(Set.of(new KeyPair("products", List.of())));
    }

    @Nested
    @DisplayName("Product Registration Tests")
    class RegistrationTests {

        @Test
        @DisplayName("Should register product successfully")
        void shouldRegisterProduct() {
            // Arrange
            ProductRequest request = ProductRequest.builder()
                    .name("Test Product")
                    .price(new BigDecimal("99.99"))
                    .build();

            // Act
            IHttpResponse<Void> response = productController.register(request, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.statusCode()).isEqualTo(201);
            assertThat(response.next()).contains("redirect:");
            assertThat(response.next()).contains("product-123");

            verify(createProductWithThumbPort).execute(request, VALID_AUTH_TOKEN);
        }
    }

    @Nested
    @DisplayName("Product Retrieval Tests")
    class RetrievalTests {

        @Test
        @DisplayName("Should find product by ID")
        void shouldFindProductById() {
            // Arrange
            ProductRequest request = ProductRequest.builder().id("product-123").build();

            // Act
            IHttpResponse<ProductResponse> response = productController.findById(request, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isNotNull();
            assertThat(response.body().getId()).isEqualTo("product-123");
            assertThat(response.next()).contains("forward:");

            verify(productDetailPort).get(request, VALID_AUTH_TOKEN);
        }

        @Test
        @DisplayName("Should list products with pagination")
        void shouldListProductsWithPagination() {
            // Arrange
            PageRequest pageRequest = PageRequest.builder()
                    .initialPage(0)
                    .pageSize(10)
                    .build();

            // Act
            IServletResponse response = productController.list(pageRequest, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isNotNull();
            assertThat(response.next()).contains("forward:");

            verify(productMapper).toProduct(null, USER_ID);
            verify(listProductContainerPort).assembleContainerResponse(any(), eq(VALID_AUTH_TOKEN), any());
        }

        @Test
        @DisplayName("Should get product details with categories")
        void shouldGetProductDetails() {
            // Arrange
            ProductRequest request = ProductRequest.builder().id("product-123").build();

            // Act
            IServletResponse response = productController.details(request, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isNotNull();
            assertThat(response.next()).contains("forward:");
            assertThat(response.next()).contains("formUpdateProduct");

            verify(productDetailPort).get(request, VALID_AUTH_TOKEN);
            verify(listCategoryPort).list(null, VALID_AUTH_TOKEN);
        }
    }

    @Nested
    @DisplayName("Product Search Tests")
    class SearchTests {

        @Test
        @DisplayName("Should search products with query parameters")
        void shouldSearchProducts() {
            // Arrange
            Map<String, String> params = new HashMap<>();
            params.put("name", "laptop");
            Query query = Query.builder().parameters(params).build();

            PageRequest pageRequest = PageRequest.builder()
                    .initialPage(0)
                    .pageSize(10)
                    .build();

            // Act
            IServletResponse response = productController.search(query, pageRequest, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isNotNull();
            assertThat(response.next()).contains("forward:");

            verify(authenticationPort).extractUser(VALID_AUTH_TOKEN);
            verify(productMapper).queryToProduct(eq(query), any());
            verify(listProductContainerPort).assembleContainerResponse(any(), eq(VALID_AUTH_TOKEN), any());
        }
    }

    @Nested
    @DisplayName("Product Update Tests")
    class UpdateTests {

        @Test
        @DisplayName("Should update product successfully")
        void shouldUpdateProduct() {
            // Arrange
            ProductRequest request = ProductRequest.builder()
                    .id("product-123")
                    .name("Updated Product")
                    .build();

            // Act
            IHttpResponse<Void> response = productController.update(request, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.statusCode()).isEqualTo(204);
            assertThat(response.next()).contains("redirect:");

            verify(updateProductPort).update(request, VALID_AUTH_TOKEN);
        }

        @Test
        @DisplayName("Should update product thumbnail")
        void shouldUpdateProductThumbnail() {
            // Arrange
            FileUploadRequest uploadRequest = new FileUploadRequest(
                    new BinaryPayload("path/to/thumb.jpg", 10L, "image/jpeg"),
                    "user-123");

            // Act
            IHttpResponse<Void> response = productController.upload(uploadRequest, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.statusCode()).isEqualTo(204);
            assertThat(response.next()).contains("redirect:");

            verify(updateProductThumbPort).updateThumb(uploadRequest, VALID_AUTH_TOKEN);
        }
    }

    @Nested
    @DisplayName("Product Deletion Tests")
    class DeletionTests {

        @Test
        @DisplayName("Should delete product successfully")
        void shouldDeleteProduct() {
            // Arrange
            ProductRequest request = ProductRequest.builder().id("product-123").build();

            doNothing().when(deleteProductPort).delete(any(), any());

            // Act
            IHttpResponse<Void> response = productController.delete(request, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.next()).contains("redirect:");

            verify(deleteProductPort).delete(request, VALID_AUTH_TOKEN);
        }
    }

    @Nested
    @DisplayName("Product Scraping Tests")
    class ScrapingTests {

        @Test
        @DisplayName("Should scrape product from URL")
        void shouldScrapeProduct() {
            // Arrange
            String url = "https://example.com/product";

            when(scrapeProductPort.scrapeAsync(any(), any())).thenReturn(null);

            // Act
            IHttpResponse<Void> response = productController.scrape(VALID_AUTH_TOKEN, url);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.next()).contains("redirect:");

            verify(scrapeProductPort).scrapeAsync(url, VALID_AUTH_TOKEN);
        }
    }

    @Nested
    @DisplayName("Forward Navigation Tests")
    class ForwardTests {

        @Test
        @DisplayName("Should forward to registration form with categories")
        void shouldForwardToRegistrationForm() {
            // Arrange
            CategoryResponse category = new CategoryResponse("cat-1");
            category.setName("Electronics");

            when(listCategoryPort.list(null, VALID_AUTH_TOKEN))
                    .thenReturn(List.of(category));

            // Act
            IHttpResponse<Collection<CategoryResponse>> response = productController.forward(VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.statusCode()).isEqualTo(302);
            assertThat(response.body()).isNotNull();
            assertThat(response.next()).contains("forward:");
            assertThat(response.next()).contains("formCreateProduct");

            verify(listCategoryPort).list(null, VALID_AUTH_TOKEN);
        }
    }

    @Nested
    @DisplayName("Implementation Tests")
    class ImplementationTests {

        @Test
        @DisplayName("Should implement ProductControllerApi interface")
        void shouldImplementInterface() {
            assertThat(productController).isInstanceOf(ProductControllerApi.class);
        }

        @Test
        @DisplayName("Should have all required dependencies injected")
        void shouldHaveAllDependencies() {
            assertThat(productController).extracting("productDetailPort").isNotNull();
            assertThat(productController).extracting("deleteProductPort").isNotNull();
            assertThat(productController).extracting("updateProductPort").isNotNull();
            assertThat(productController).extracting("createProductWithThumbPort").isNotNull();
            assertThat(productController).extracting("scrapeProductPort").isNotNull();
            assertThat(productController).extracting("listCategoryPort").isNotNull();
            assertThat(productController).extracting("listProductContainerPort").isNotNull();
            assertThat(productController).extracting("productMapper").isNotNull();
            assertThat(productController).extracting("updateProductThumbPort").isNotNull();
        }
    }
}

