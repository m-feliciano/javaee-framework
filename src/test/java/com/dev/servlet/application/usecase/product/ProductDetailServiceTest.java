package com.dev.servlet.application.usecase.product;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.exception.NotFoundException;
import com.dev.servlet.application.mapper.ProductMapper;
import com.dev.servlet.application.port.out.product.ProductRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.request.ProductRequest;
import com.dev.servlet.application.transfer.response.ProductResponse;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.Status;
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
import java.time.LocalDate;
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
@DisplayName("ProductDetailUseCase Tests")
class ProductDetailServiceTest {

    private static final UUID USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final String AUTH_TOKEN = "Bearer valid.token";
    private static final UUID PRODUCT_ID = UUID.fromString("223e4567-e89b-12d3-a456-426614174000");
    @Mock
    private ProductRepositoryPort repositoryPort;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private AuthenticationPort authenticationPort;
    @InjectMocks
    private ProductDetailService productDetailService;
    private ProductRequest productRequest;
    private Product product;
    private Product foundProduct;

    @BeforeEach
    void setUp() {
        productRequest = ProductRequest.builder()
                .id(PRODUCT_ID)
                .build();

        product = Product.builder()
                .id(PRODUCT_ID)
                .owner(User.builder().id(USER_ID).build())
                .status(Status.ACTIVE.getValue())
                .build();

        foundProduct = Product.builder()
                .id(PRODUCT_ID)
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .description("Test description")
                .owner(User.builder().id(USER_ID).build())
                .registerDate(LocalDate.now())
                .status(Status.ACTIVE.getValue())
                .build();

        ProductResponse productResponse = new ProductResponse(PRODUCT_ID);
        productResponse.setName("Test Product");
        productResponse.setPrice(new BigDecimal("99.99"));

        lenient().when(authenticationPort.extractUserId(AUTH_TOKEN)).thenReturn(USER_ID);
        lenient().when(productMapper.toProduct(any(ProductRequest.class), eq(USER_ID))).thenReturn(product);
        lenient().when(repositoryPort.find(any(Product.class))).thenReturn(Optional.of(foundProduct));
        lenient().when(productMapper.toResponse(any(Product.class))).thenReturn(productResponse);
    }

    @Nested
    @DisplayName("Successful Retrieval Tests")
    class SuccessfulRetrievalTests {

        @Test
        @DisplayName("Should get product details successfully")
        void shouldGetProductDetailsSuccessfully() {
            // Act
            ProductResponse response = productDetailService.get(productRequest, AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(PRODUCT_ID);
            assertThat(response.getName()).isEqualTo("Test Product");
            assertThat(response.getPrice()).isEqualTo(new BigDecimal("99.99"));

            verify(authenticationPort).extractUserId(AUTH_TOKEN);
            verify(productMapper).toProduct(productRequest, USER_ID);
            verify(repositoryPort).find(any(Product.class));
            verify(productMapper).toResponse(foundProduct);
        }

        @Test
        @DisplayName("Should extract user ID from auth token")
        void shouldExtractUserId() {
            // Act
            productDetailService.get(productRequest, AUTH_TOKEN);

            // Assert
            verify(authenticationPort).extractUserId(AUTH_TOKEN);
        }

        @Test
        @DisplayName("Should map request to product entity")
        void shouldMapRequestToEntity() {
            // Act
            productDetailService.get(productRequest, AUTH_TOKEN);

            // Assert
            verify(productMapper).toProduct(productRequest, USER_ID);
        }

        @Test
        @DisplayName("Should set status to ACTIVE before searching")
        void shouldSetStatusToActive() {
            // Act
            productDetailService.get(productRequest, AUTH_TOKEN);

            // Assert
            verify(repositoryPort).find(argThat(p ->
                    Status.ACTIVE.getValue().equals(p.getStatus())
            ));
        }

        @Test
        @DisplayName("Should find product in repository")
        void shouldFindProduct() {
            // Act
            productDetailService.get(productRequest, AUTH_TOKEN);

            // Assert
            verify(repositoryPort).find(product);
        }

        @Test
        @DisplayName("Should map found product to response")
        void shouldMapProductToResponse() {
            // Act
            productDetailService.get(productRequest, AUTH_TOKEN);

            // Assert
            verify(productMapper).toResponse(foundProduct);
        }
    }

    @Nested
    @DisplayName("Product Not Found Tests")
    class ProductNotFoundTests {

        @Test
        @DisplayName("Should throw AppException when product not found")
        void shouldThrowExceptionWhenProductNotFound() {
            // Arrange
            when(repositoryPort.find(any(Product.class))).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> productDetailService.get(productRequest, AUTH_TOKEN))
                    .isInstanceOf(NotFoundException.class);

            verify(repositoryPort).find(any(Product.class));
            verify(productMapper, never()).toResponse(any());
        }

        @Test
        @DisplayName("Should not map to response when product not found")
        void shouldNotMapWhenNotFound() {
            // Arrange
            when(repositoryPort.find(any(Product.class))).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> productDetailService.get(productRequest, AUTH_TOKEN))
                    .isInstanceOf(AppException.class);

            verify(productMapper, never()).toResponse(any());
        }
    }

    @Nested
    @DisplayName("Different Product Types Tests")
    class DifferentProductTypesTests {

        @Test
        @DisplayName("Should get product with all details")
        void shouldGetProductWithAllDetails() {
            // Arrange
            Product completeProduct = Product.builder()
                    .id(PRODUCT_ID)
                    .name("Complete Product")
                    .price(new BigDecimal("199.99"))
                    .description("Complete description")
                    .owner(User.builder().id(USER_ID).build())
                    .registerDate(LocalDate.now())
                    .status(Status.ACTIVE.getValue())
                    .build();

            ProductResponse completeResponse = new ProductResponse(PRODUCT_ID);
            completeResponse.setName("Complete Product");
            completeResponse.setPrice(new BigDecimal("199.99"));
            completeResponse.setDescription("Complete description");
            completeResponse.setThumbUrl("https://example.com/image.jpg");

            when(repositoryPort.find(any(Product.class))).thenReturn(Optional.of(completeProduct));
            when(productMapper.toResponse(completeProduct)).thenReturn(completeResponse);

            // Act
            ProductResponse response = productDetailService.get(productRequest, AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("Complete Product");
            assertThat(response.getDescription()).isEqualTo("Complete description");
            assertThat(response.getThumbUrl()).isEqualTo("https://example.com/image.jpg");
        }

        @Test
        @DisplayName("Should get product with minimal details")
        void shouldGetProductWithMinimalDetails() {
            // Arrange
            Product minimalProduct = Product.builder()
                    .id(PRODUCT_ID)
                    .name("Minimal Product")
                    .owner(User.builder().id(USER_ID).build())
                    .status(Status.ACTIVE.getValue())
                    .build();

            ProductResponse minimalResponse = new ProductResponse(PRODUCT_ID);
            minimalResponse.setName("Minimal Product");

            when(repositoryPort.find(any(Product.class))).thenReturn(Optional.of(minimalProduct));
            when(productMapper.toResponse(minimalProduct)).thenReturn(minimalResponse);

            // Act
            ProductResponse response = productDetailService.get(productRequest, AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("Minimal Product");
        }
    }

    @Nested
    @DisplayName("Execution Order Tests")
    class ExecutionOrderTests {

        @Test
        @DisplayName("Should execute operations in correct order")
        void shouldExecuteInCorrectOrder() {
            // Act
            productDetailService.get(productRequest, AUTH_TOKEN);

            // Assert - Verify order
            var inOrder = inOrder(authenticationPort, productMapper, repositoryPort);
            inOrder.verify(authenticationPort).extractUserId(AUTH_TOKEN);
            inOrder.verify(productMapper).toProduct(productRequest, USER_ID);
            inOrder.verify(repositoryPort).find(any(Product.class));
            inOrder.verify(productMapper).toResponse(foundProduct);
        }
    }

    @Nested
    @DisplayName("User Context Tests")
    class UserContextTests {

        @Test
        @DisplayName("Should use extracted user ID in search")
        void shouldUseExtractedUserId() {
            // Act
            productDetailService.get(productRequest, AUTH_TOKEN);

            // Assert
            verify(productMapper).toProduct(productRequest, USER_ID);
            verify(repositoryPort).find(argThat(p ->
                    p.getOwner() != null && USER_ID.equals(p.getOwner().getId())
            ));
        }

        @Test
        @DisplayName("Should search for ACTIVE products only")
        void shouldSearchActiveProductsOnly() {
            // Act
            productDetailService.get(productRequest, AUTH_TOKEN);

            // Assert
            verify(repositoryPort).find(argThat(p ->
                    Status.ACTIVE.getValue().equals(p.getStatus())
            ));
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle product with null optional fields")
        void shouldHandleNullOptionalFields() {
            // Arrange
            Product productWithNulls = Product.builder()
                    .id(PRODUCT_ID)
                    .name("Product")
                    .owner(User.builder().id(USER_ID).build())
                    .status(Status.ACTIVE.getValue())
                    .build();

            when(repositoryPort.find(any(Product.class))).thenReturn(Optional.of(productWithNulls));

            // Act
            ProductResponse response = productDetailService.get(productRequest, AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            verify(productMapper).toResponse(productWithNulls);
        }
    }
}

