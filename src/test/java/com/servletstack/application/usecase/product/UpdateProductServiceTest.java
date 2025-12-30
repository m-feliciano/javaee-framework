package com.servletstack.application.usecase.product;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.exception.NotFoundException;
import com.servletstack.application.mapper.ProductMapper;
import com.servletstack.application.port.out.product.ProductRepositoryPort;
import com.servletstack.application.port.out.security.AuthenticationPort;
import com.servletstack.application.transfer.request.CategoryRequest;
import com.servletstack.application.transfer.request.ProductRequest;
import com.servletstack.application.transfer.response.ProductResponse;
import com.servletstack.domain.entity.Category;
import com.servletstack.domain.entity.Product;
import com.servletstack.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("UpdateProductUseCase Tests")
class UpdateProductServiceTest {

    private static final String AUTH_TOKEN = "Bearer valid.token";
    private static final UUID USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID PRODUCT_ID = UUID.fromString("223e4567-e89b-12d3-a456-426614174000");
    private static final UUID CATEGORY_ID = UUID.fromString("323e4567-e89b-12d3-a456-426614174000");
    @Mock
    private ProductRepositoryPort productRepositoryPort;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private AuthenticationPort authenticationPort;
    @InjectMocks
    private UpdateProductService updateProductService;
    private ProductRequest updateRequest;
    private Product searchProduct;

    @BeforeEach
    void setUp() {
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .id(CATEGORY_ID)
                .build();

        updateRequest = ProductRequest.builder()
                .id(PRODUCT_ID)
                .name("Updated Product")
                .description("Updated description")
                .price(new BigDecimal("149.99"))
                .category(categoryRequest)
                .build();

        searchProduct = Product.builder()
                .id(PRODUCT_ID)
                .owner(User.builder().id(USER_ID).build())
                .build();

        Product existingProduct = Product.builder()
                .id(PRODUCT_ID)
                .name("Old Product")
                .description("Old description")
                .price(new BigDecimal("99.99"))
                .owner(User.builder().id(USER_ID).build())
                .category(Category.builder().id(CATEGORY_ID).build())
                .build();

        lenient()
                .when(authenticationPort.extractUserId(AUTH_TOKEN))
                .thenReturn(USER_ID);
        lenient()
                .when(productMapper.toProduct(any(ProductRequest.class), eq(USER_ID)))
                .thenReturn(searchProduct);
        lenient()
                .when(productRepositoryPort.find(any(Product.class)))
                .thenReturn(Optional.of(existingProduct));
        lenient()
                .when(productRepositoryPort.update(any(Product.class)))
                .thenReturn(existingProduct);
    }

    @Nested
    @DisplayName("Successful Update Tests")
    class SuccessfulUpdateTests {

        @Test
        @DisplayName("Should update product successfully")
        void shouldUpdateProductSuccessfully() {
            // Act
            ProductResponse response = updateProductService.update(updateRequest, AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(PRODUCT_ID);

            verify(authenticationPort).extractUserId(AUTH_TOKEN);
            verify(productMapper).toProduct(updateRequest, USER_ID);
            verify(productRepositoryPort).find(searchProduct);
            verify(productRepositoryPort).update(any(Product.class));
        }

        @Test
        @DisplayName("Should update product name")
        void shouldUpdateProductName() {
            // Act
            updateProductService.update(updateRequest, AUTH_TOKEN);

            // Assert
            verify(productRepositoryPort).update(argThat(p ->
                    "Updated Product".equals(p.getName())
            ));
        }

        @Test
        @DisplayName("Should update product description")
        void shouldUpdateProductDescription() {
            // Act
            updateProductService.update(updateRequest, AUTH_TOKEN);

            // Assert
            verify(productRepositoryPort).update(argThat(p ->
                    "Updated description".equals(p.getDescription())
            ));
        }

        @Test
        @DisplayName("Should update product price")
        void shouldUpdateProductPrice() {
            // Act
            updateProductService.update(updateRequest, AUTH_TOKEN);

            // Assert
            verify(productRepositoryPort).update(argThat(p ->
                    new BigDecimal("149.99").equals(p.getPrice())
            ));
        }

        @Test
        @DisplayName("Should update product category")
        void shouldUpdateProductCategory() {
            // Act
            updateProductService.update(updateRequest, AUTH_TOKEN);

            // Assert
            verify(productRepositoryPort).update(argThat(p ->
                    p.getCategory() != null &&
                    CATEGORY_ID.equals(p.getCategory().getId())
            ));
        }

        @Test
        @DisplayName("Should extract user ID from auth token")
        void shouldExtractUserId() {
            // Act
            updateProductService.update(updateRequest, AUTH_TOKEN);

            // Assert
            verify(authenticationPort).extractUserId(AUTH_TOKEN);
        }

        @Test
        @DisplayName("Should find existing product before updating")
        void shouldFindExistingProduct() {
            // Act
            updateProductService.update(updateRequest, AUTH_TOKEN);

            // Assert
            verify(productRepositoryPort).find(searchProduct);
        }
    }

    @Nested
    @DisplayName("Product Not Found Tests")
    class ProductNotFoundTests {

        @Test
        @DisplayName("Should throw AppException when product not found")
        void shouldThrowExceptionWhenProductNotFound() {
            // Arrange
            when(productRepositoryPort.find(any(Product.class))).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> updateProductService.update(updateRequest, AUTH_TOKEN))
                    .isInstanceOf(NotFoundException.class);

            verify(productRepositoryPort).find(any(Product.class));
            verify(productRepositoryPort, never()).update(any());
        }

        @Test
        @DisplayName("Should not update when product not found")
        void shouldNotUpdateWhenNotFound() {
            // Arrange
            when(productRepositoryPort.find(any(Product.class))).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> updateProductService.update(updateRequest, AUTH_TOKEN))
                    .isInstanceOf(AppException.class);

            verify(productRepositoryPort, never()).update(any());
        }
    }

    @Nested
    @DisplayName("Partial Update Tests")
    class PartialUpdateTests {

        @Test
        @DisplayName("Should update only changed fields")
        void shouldUpdateOnlyChangedFields() {
            // Arrange
            ProductRequest partialUpdate = ProductRequest.builder()
                    .id(PRODUCT_ID)
                    .name("New Name Only")
                    .description(updateRequest.description())
                    .price(updateRequest.price())
                    .category(updateRequest.category())
                    .build();

            // Act
            updateProductService.update(partialUpdate, AUTH_TOKEN);

            // Assert
            verify(productRepositoryPort).update(argThat(p ->
                    "New Name Only".equals(p.getName())
            ));
        }

        @Test
        @DisplayName("Should handle price update to zero")
        void shouldHandlePriceUpdateToZero() {
            // Arrange
            ProductRequest zeroPrice = ProductRequest.builder()
                    .id(PRODUCT_ID)
                    .name(updateRequest.name())
                    .description(updateRequest.description())
                    .price(BigDecimal.ZERO)
                    .category(updateRequest.category())
                    .build();

            // Act
            updateProductService.update(zeroPrice, AUTH_TOKEN);

            // Assert
            verify(productRepositoryPort).update(argThat(p ->
                    BigDecimal.ZERO.equals(p.getPrice())
            ));
        }

        @Test
        @DisplayName("Should handle very high price")
        void shouldHandleHighPrice() {
            // Arrange
            ProductRequest highPrice = ProductRequest.builder()
                    .id(PRODUCT_ID)
                    .name(updateRequest.name())
                    .description(updateRequest.description())
                    .price(new BigDecimal("999999.99"))
                    .category(updateRequest.category())
                    .build();

            // Act
            updateProductService.update(highPrice, AUTH_TOKEN);

            // Assert
            verify(productRepositoryPort).update(argThat(p ->
                    new BigDecimal("999999.99").equals(p.getPrice())
            ));
        }
    }

    @Nested
    @DisplayName("Execution Order Tests")
    class ExecutionOrderTests {

        @Test
        @DisplayName("Should execute operations in correct order")
        void shouldExecuteInCorrectOrder() {
            // Act
            updateProductService.update(updateRequest, AUTH_TOKEN);

            // Assert - Verify order
            var inOrder = inOrder(authenticationPort, productMapper, productRepositoryPort);
            inOrder.verify(authenticationPort).extractUserId(AUTH_TOKEN);
            inOrder.verify(productMapper).toProduct(updateRequest, USER_ID);
            inOrder.verify(productRepositoryPort).find(any(Product.class));
            inOrder.verify(productRepositoryPort).update(any(Product.class));
        }

        @Test
        @DisplayName("Should find before updating")
        void shouldFindBeforeUpdating() {
            // Act
            updateProductService.update(updateRequest, AUTH_TOKEN);

            // Assert
            var inOrder = inOrder(productRepositoryPort);
            inOrder.verify(productRepositoryPort).find(any(Product.class));
            inOrder.verify(productRepositoryPort).update(any(Product.class));
        }
    }

    @Nested
    @DisplayName("Category Update Tests")
    class CategoryUpdateTests {

        @Test
        @DisplayName("Should create category with correct ID")
        void shouldCreateCategoryWithCorrectId() {
            // Act
            updateProductService.update(updateRequest, AUTH_TOKEN);

            // Assert
            verify(productRepositoryPort).update(argThat(p ->
                    p.getCategory() != null &&
                    CATEGORY_ID.equals(p.getCategory().getId())
            ));
        }

        @Test
        @DisplayName("Should handle category change")
        void shouldHandleCategoryChange() {
            // Arrange
            UUID newCategoryId = UUID.fromString("423e4567-e89b-12d3-a456-426614174000");
            CategoryRequest newCategory = CategoryRequest.builder()
                    .id(newCategoryId)
                    .build();

            ProductRequest requestWithNewCategory = ProductRequest.builder()
                    .id(PRODUCT_ID)
                    .name(updateRequest.name())
                    .description(updateRequest.description())
                    .price(updateRequest.price())
                    .category(newCategory)
                    .build();

            // Act
            updateProductService.update(requestWithNewCategory, AUTH_TOKEN);

            // Assert
            verify(productRepositoryPort).update(argThat(p ->
                    p.getCategory() != null &&
                    newCategoryId.equals(p.getCategory().getId())
            ));
        }
    }
}

