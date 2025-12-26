package com.dev.servlet.application.usecase.product;

import com.dev.servlet.application.mapper.ProductMapper;
import com.dev.servlet.application.port.out.product.ProductRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.request.CategoryRequest;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("RegisterProductUseCase Tests")
class RegisterProductUseCaseTest {

    private static final String AUTH_TOKEN = "Bearer valid.token";
    private static final UUID USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID PRODUCT_ID = UUID.fromString("223e4567-e89b-12d3-a456-426614174000");
    @Mock
    private ProductRepositoryPort repositoryPort;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private AuthenticationPort authenticationPort;
    @InjectMocks
    private RegisterProductUseCase registerProductUseCase;
    private ProductRequest productRequest;
    private Product product;

    @BeforeEach
    void setUp() {
        productRequest = ProductRequest.builder()
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .description("Test description")
                .build();

        product = Product.builder()
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .description("Test description")
                .owner(User.builder().id(USER_ID).build())
                .build();

        Product savedProduct = Product.builder()
                .id(PRODUCT_ID)
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .description("Test description")
                .owner(User.builder().id(USER_ID).build())
                .registerDate(LocalDate.now())
                .status(Status.ACTIVE.getValue())
                .build();

        lenient().when(authenticationPort.extractUserId(AUTH_TOKEN)).thenReturn(USER_ID);
        lenient().when(productMapper.toProduct(any(ProductRequest.class), eq(USER_ID))).thenReturn(product);
        lenient().when(repositoryPort.save(any(Product.class))).thenReturn(savedProduct);
    }

    @Nested
    @DisplayName("Successful Registration Tests")
    class SuccessfulRegistrationTests {

        @Test
        @DisplayName("Should register product successfully")
        void shouldRegisterProductSuccessfully() {
            // Act
            ProductResponse response = registerProductUseCase.register(productRequest, AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(PRODUCT_ID);

            verify(authenticationPort).extractUserId(AUTH_TOKEN);
            verify(productMapper).toProduct(productRequest, USER_ID);
            verify(repositoryPort).save(any(Product.class));
        }

        @Test
        @DisplayName("Should set register date to current date")
        void shouldSetRegisterDate() {
            // Arrange
            LocalDate before = LocalDate.now();

            // Act
            registerProductUseCase.register(productRequest, AUTH_TOKEN);

            // Assert
            verify(repositoryPort).save(argThat(p ->
                    p.getRegisterDate() != null &&
                    !p.getRegisterDate().isBefore(before)
            ));
        }

        @Test
        @DisplayName("Should set product status to ACTIVE")
        void shouldSetStatusToActive() {
            // Act
            registerProductUseCase.register(productRequest, AUTH_TOKEN);

            // Assert
            verify(repositoryPort).save(argThat(p ->
                    Status.ACTIVE.getValue().equals(p.getStatus())
            ));
        }

        @Test
        @DisplayName("Should extract user ID from auth token")
        void shouldExtractUserId() {
            // Act
            registerProductUseCase.register(productRequest, AUTH_TOKEN);

            // Assert
            verify(authenticationPort).extractUserId(AUTH_TOKEN);
            verify(productMapper).toProduct(productRequest, USER_ID);
        }

        @Test
        @DisplayName("Should map request to product entity")
        void shouldMapRequestToEntity() {
            // Act
            registerProductUseCase.register(productRequest, AUTH_TOKEN);

            // Assert
            verify(productMapper).toProduct(eq(productRequest), eq(USER_ID));
        }

        @Test
        @DisplayName("Should save product to repository")
        void shouldSaveProduct() {
            // Act
            registerProductUseCase.register(productRequest, AUTH_TOKEN);

            // Assert
            verify(repositoryPort).save(product);
        }

        @Test
        @DisplayName("Should return response with saved product ID")
        void shouldReturnResponseWithId() {
            // Act
            ProductResponse response = registerProductUseCase.register(productRequest, AUTH_TOKEN);

            // Assert
            assertThat(response.getId()).isEqualTo(PRODUCT_ID);
        }
    }

    @Nested
    @DisplayName("Product Data Validation Tests")
    class ProductDataValidationTests {

        @Test
        @DisplayName("Should handle product with all fields")
        void shouldHandleProductWithAllFields() {
            // Arrange
            ProductRequest fullRequest = ProductRequest.builder()
                    .name("Complete Product")
                    .price(new BigDecimal("149.99"))
                    .description("Complete description")
                    .category(new CategoryRequest(null, "Electronics", null))
                    .build();

            Product fullProduct = Product.builder()
                    .name("Complete Product")
                    .price(new BigDecimal("149.99"))
                    .owner(User.builder().id(USER_ID).build())
                    .build();

            when(productMapper.toProduct(fullRequest, USER_ID)).thenReturn(fullProduct);

            // Act
            ProductResponse response = registerProductUseCase.register(fullRequest, AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            verify(repositoryPort).save(fullProduct);
        }

        @Test
        @DisplayName("Should handle product with minimal fields")
        void shouldHandleProductWithMinimalFields() {
            // Arrange
            ProductRequest minimalRequest = ProductRequest.builder()
                    .name("Minimal Product")
                    .build();

            Product minimalProduct = Product.builder()
                    .name("Minimal Product")
                    .owner(User.builder().id(USER_ID).build())
                    .build();

            when(productMapper.toProduct(minimalRequest, USER_ID)).thenReturn(minimalProduct);

            // Act
            ProductResponse response = registerProductUseCase.register(minimalRequest, AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            verify(repositoryPort).save(minimalProduct);
        }
    }

    @Nested
    @DisplayName("Execution Order Tests")
    class ExecutionOrderTests {

        @Test
        @DisplayName("Should execute operations in correct order")
        void shouldExecuteInCorrectOrder() {
            // Act
            registerProductUseCase.register(productRequest, AUTH_TOKEN);

            // Assert - Verify order
            var inOrder = inOrder(authenticationPort, productMapper, repositoryPort);
            inOrder.verify(authenticationPort).extractUserId(AUTH_TOKEN);
            inOrder.verify(productMapper).toProduct(productRequest, USER_ID);
            inOrder.verify(repositoryPort).save(any(Product.class));
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle very long product name")
        void shouldHandleLongProductName() {
            // Arrange
            String longName = "A".repeat(200);
            ProductRequest request = ProductRequest.builder()
                    .name(longName)
                    .build();

            Product productWithLongName = Product.builder()
                    .name(longName)
                    .owner(User.builder().id(USER_ID).build())
                    .build();

            when(productMapper.toProduct(request, USER_ID)).thenReturn(productWithLongName);

            // Act
            ProductResponse response = registerProductUseCase.register(request, AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            verify(repositoryPort).save(productWithLongName);
        }

        @Test
        @DisplayName("Should handle zero price")
        void shouldHandleZeroPrice() {
            // Arrange
            ProductRequest request = ProductRequest.builder()
                    .name("Free Product")
                    .price(BigDecimal.ZERO)
                    .build();

            Product freeProduct = Product.builder()
                    .name("Free Product")
                    .price(BigDecimal.ZERO)
                    .owner(User.builder().id(USER_ID).build())
                    .build();

            when(productMapper.toProduct(request, USER_ID)).thenReturn(freeProduct);

            // Act
            ProductResponse response = registerProductUseCase.register(request, AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            verify(repositoryPort).save(freeProduct);
        }

        @Test
        @DisplayName("Should handle very high price")
        void shouldHandleHighPrice() {
            // Arrange
            ProductRequest request = ProductRequest.builder()
                    .name("Expensive Product")
                    .price(new BigDecimal("999999.99"))
                    .build();

            Product expensiveProduct = Product.builder()
                    .name("Expensive Product")
                    .price(new BigDecimal("999999.99"))
                    .owner(User.builder().id(USER_ID).build())
                    .build();

            when(productMapper.toProduct(request, USER_ID)).thenReturn(expensiveProduct);

            // Act
            ProductResponse response = registerProductUseCase.register(request, AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            verify(repositoryPort).save(expensiveProduct);
        }
    }
}

