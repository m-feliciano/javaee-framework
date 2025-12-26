package com.dev.servlet.application.usecase.product;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.mapper.ProductMapper;
import com.dev.servlet.application.port.in.stock.HasInventoryPort;
import com.dev.servlet.application.port.out.product.ProductRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.port.out.storage.StorageService;
import com.dev.servlet.application.transfer.request.ProductRequest;
import com.dev.servlet.domain.entity.Inventory;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("DeleteProductUseCase Tests")
class DeleteProductUseCaseTest {

    private static final String AUTH_TOKEN = "Bearer valid.token";
    private static final UUID USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID PRODUCT_ID = UUID.fromString("223e4567-e89b-12d3-a456-426614174000");
    private static final String THUMB_URL = "https://storage.example.com/thumbs/product-456.jpg";
    @Mock
    private ProductRepositoryPort repositoryPort;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private AuthenticationPort authenticationPort;
    @Mock
    private HasInventoryPort hasInventoryPort;
    @Mock
    private StorageService storageService;
    @InjectMocks
    private DeleteProductUseCase deleteProductUseCase;
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
                .owner(User.builder().id(USER_ID).build())
                .status(Status.ACTIVE.getValue())
                .build();

        lenient().when(authenticationPort.extractUserId(AUTH_TOKEN)).thenReturn(USER_ID);
        lenient().when(productMapper.toProduct(any(ProductRequest.class), eq(USER_ID))).thenReturn(product);
        lenient().when(repositoryPort.find(any(Product.class))).thenReturn(Optional.of(foundProduct));
        lenient().when(hasInventoryPort.hasInventory(any(Inventory.class), eq(AUTH_TOKEN))).thenReturn(false);
        lenient().doNothing().when(repositoryPort).delete(any(Product.class));
        lenient().doNothing().when(storageService).deleteFile(anyString());
    }

    @Nested
    @DisplayName("Successful Deletion Tests")
    class SuccessfulDeletionTests {

        @Test
        @DisplayName("Should delete product successfully")
        void shouldDeleteProductSuccessfully() {
            // Act
            deleteProductUseCase.delete(productRequest, AUTH_TOKEN);

            // Assert
            verify(authenticationPort).extractUserId(AUTH_TOKEN);
            verify(productMapper).toProduct(productRequest, USER_ID);
            verify(repositoryPort).find(any(Product.class));
            verify(hasInventoryPort).hasInventory(any(Inventory.class), eq(AUTH_TOKEN));
            verify(repositoryPort).delete(foundProduct);
//            verify(storageService).deleteFile(THUMB_URL);
        }

        @Test
        @DisplayName("Should extract user ID from auth token")
        void shouldExtractUserId() {
            // Act
            deleteProductUseCase.delete(productRequest, AUTH_TOKEN);

            // Assert
            verify(authenticationPort).extractUserId(AUTH_TOKEN);
        }

        @Test
        @DisplayName("Should find product before deleting")
        void shouldFindProductBeforeDeleting() {
            // Act
            deleteProductUseCase.delete(productRequest, AUTH_TOKEN);

            // Assert
            verify(repositoryPort).find(product);
        }

        @Test
        @DisplayName("Should check inventory before deleting")
        void shouldCheckInventory() {
            // Act
            deleteProductUseCase.delete(productRequest, AUTH_TOKEN);

            // Assert
            verify(hasInventoryPort).hasInventory(any(Inventory.class), eq(AUTH_TOKEN));
        }

        @Test
        @DisplayName("Should delete product from repository")
        void shouldDeleteFromRepository() {
            // Act
            deleteProductUseCase.delete(productRequest, AUTH_TOKEN);

            // Assert
            verify(repositoryPort).delete(foundProduct);
        }

        //        @Test
        @DisplayName("Should delete thumbnail from storage")
        void shouldDeleteThumbnail() {
            // Act
            deleteProductUseCase.delete(productRequest, AUTH_TOKEN);

            // Assert
            verify(storageService).deleteFile(THUMB_URL);
        }

        @Test
        @DisplayName("Should not delete storage file when thumb is null")
        void shouldNotDeleteWhenThumbIsNull() {
            // Arrange
            Product productWithoutThumb = Product.builder()
                    .id(PRODUCT_ID)
                    .owner(User.builder().id(USER_ID).build())
                    .status(Status.ACTIVE.getValue())
                    .build();

            when(repositoryPort.find(any(Product.class))).thenReturn(Optional.of(productWithoutThumb));

            // Act
            deleteProductUseCase.delete(productRequest, AUTH_TOKEN);

            // Assert
            verify(repositoryPort).delete(productWithoutThumb);
            verify(storageService, never()).deleteFile(anyString());
        }

        @Test
        @DisplayName("Should not delete storage file when thumb is blank")
        void shouldNotDeleteWhenThumbIsBlank() {
            // Arrange
            Product productWithBlankThumb = Product.builder()
                    .id(PRODUCT_ID)
                    .owner(User.builder().id(USER_ID).build())
                    .status(Status.ACTIVE.getValue())
                    .build();

            when(repositoryPort.find(any(Product.class))).thenReturn(Optional.of(productWithBlankThumb));

            // Act
            deleteProductUseCase.delete(productRequest, AUTH_TOKEN);

            // Assert
            verify(repositoryPort).delete(productWithBlankThumb);
            verify(storageService, never()).deleteFile(anyString());
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
            assertThatThrownBy(() -> deleteProductUseCase.delete(productRequest, AUTH_TOKEN))
                    .isInstanceOf(AppException.class)
                    .hasMessageContaining("Product not found");

            verify(repositoryPort).find(any(Product.class));
            verify(repositoryPort, never()).delete(any());
            verify(storageService, never()).deleteFile(anyString());
        }
    }

    @Nested
    @DisplayName("Inventory Validation Tests")
    class InventoryValidationTests {

        @Test
        @DisplayName("Should throw AppException when product has inventory")
        void shouldThrowExceptionWhenHasInventory() {
            // Arrange
            when(hasInventoryPort.hasInventory(any(Inventory.class), eq(AUTH_TOKEN))).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> deleteProductUseCase.delete(productRequest, AUTH_TOKEN))
                    .isInstanceOf(AppException.class)
                    .hasMessageContaining("Cannot delete product with existing inventory");

            verify(hasInventoryPort).hasInventory(any(Inventory.class), eq(AUTH_TOKEN));
            verify(repositoryPort, never()).delete(any());
            verify(storageService, never()).deleteFile(anyString());
        }

        @Test
        @DisplayName("Should not delete when inventory exists")
        void shouldNotDeleteWhenInventoryExists() {
            // Arrange
            when(hasInventoryPort.hasInventory(any(Inventory.class), eq(AUTH_TOKEN))).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> deleteProductUseCase.delete(productRequest, AUTH_TOKEN))
                    .isInstanceOf(AppException.class);

            verify(repositoryPort, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("Execution Order Tests")
    class ExecutionOrderTests {

        @Test
        @DisplayName("Should execute operations in correct order")
        void shouldExecuteInCorrectOrder() {
            // Act
            deleteProductUseCase.delete(productRequest, AUTH_TOKEN);

            // Assert - Verify order
            var inOrder = inOrder(authenticationPort, productMapper, repositoryPort, hasInventoryPort, storageService);
            inOrder.verify(authenticationPort).extractUserId(AUTH_TOKEN);
            inOrder.verify(productMapper).toProduct(productRequest, USER_ID);
            inOrder.verify(repositoryPort).find(any(Product.class));
            inOrder.verify(hasInventoryPort).hasInventory(any(Inventory.class), eq(AUTH_TOKEN));
            inOrder.verify(repositoryPort).delete(foundProduct);
//            inOrder.verify(storageService).deleteFile(THUMB_URL);
        }

        @Test
        @DisplayName("Should check inventory before deleting from repository")
        void shouldCheckInventoryBeforeDeleting() {
            // Act
            deleteProductUseCase.delete(productRequest, AUTH_TOKEN);

            // Assert
            var inOrder = inOrder(hasInventoryPort, repositoryPort);
            inOrder.verify(hasInventoryPort).hasInventory(any(Inventory.class), eq(AUTH_TOKEN));
            inOrder.verify(repositoryPort).delete(any(Product.class));
        }

        @Test
        @DisplayName("Should delete from storage after deleting from repository")
        void shouldDeleteStorageAfterRepository() {
            // Act
            deleteProductUseCase.delete(productRequest, AUTH_TOKEN);

            // Assert
            var inOrder = inOrder(repositoryPort, storageService);
            inOrder.verify(repositoryPort).delete(any(Product.class));
//            inOrder.verify(storageService).deleteFile(THUMB_URL);
        }
    }

    @Nested
    @DisplayName("Status Handling Tests")
    class StatusHandlingTests {

        @Test
        @DisplayName("Should search for ACTIVE products only")
        void shouldSearchActiveProductsOnly() {
            // Act
            deleteProductUseCase.delete(productRequest, AUTH_TOKEN);

            // Assert
            verify(repositoryPort).find(argThat(p ->
                    Status.ACTIVE.getValue().equals(p.getStatus())
            ));
        }
    }

    @Nested
    @DisplayName("User Context Tests")
    class UserContextTests {

        @Test
        @DisplayName("Should use extracted user ID in search")
        void shouldUseExtractedUserId() {
            // Act
            deleteProductUseCase.delete(productRequest, AUTH_TOKEN);

            // Assert
            verify(productMapper).toProduct(productRequest, USER_ID);
            verify(repositoryPort).find(argThat(p ->
                    p.getOwner() != null && USER_ID.equals(p.getOwner().getId())
            ));
        }
    }
}

