package com.dev.servlet.adapter.out.inventory;

import com.dev.servlet.application.port.out.inventory.InventoryRepositoryPort;
import com.dev.servlet.domain.entity.Inventory;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("HasInventoryAdapter Tests")
class HasInventoryAdapterTest {

    private static final String AUTH_TOKEN = "Bearer valid.token";
    @Mock
    private InventoryRepositoryPort repositoryPort;
    @InjectMocks
    private HasInventoryAdapter hasInventoryAdapter;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = Inventory.builder()
                .id("inventory-123")
                .user(User.builder().id("user-123").build())
                .product(Product.builder().id("product-456").build())
                .quantity(10)
                .build();
    }

    @Nested
    @DisplayName("Inventory Check Tests")
    class InventoryCheckTests {

        @Test
        @DisplayName("Should return true when inventory exists")
        void shouldReturnTrueWhenInventoryExists() {
            // Arrange
            when(repositoryPort.has(inventory)).thenReturn(true);

            // Act
            boolean result = hasInventoryAdapter.hasInventory(inventory, AUTH_TOKEN);

            // Assert
            assertThat(result).isTrue();
            verify(repositoryPort).has(inventory);
        }

        @Test
        @DisplayName("Should return false when inventory does not exist")
        void shouldReturnFalseWhenInventoryDoesNotExist() {
            // Arrange
            when(repositoryPort.has(inventory)).thenReturn(false);

            // Act
            boolean result = hasInventoryAdapter.hasInventory(inventory, AUTH_TOKEN);

            // Assert
            assertThat(result).isFalse();
            verify(repositoryPort).has(inventory);
        }

        @Test
        @DisplayName("Should delegate to repository")
        void shouldDelegateToRepository() {
            // Arrange
            when(repositoryPort.has(inventory)).thenReturn(true);

            // Act
            hasInventoryAdapter.hasInventory(inventory, AUTH_TOKEN);

            // Assert
            verify(repositoryPort).has(inventory);
        }
    }

    @Nested
    @DisplayName("Different Inventory States Tests")
    class DifferentInventoryStatesTests {

        @Test
        @DisplayName("Should check inventory with zero quantity")
        void shouldCheckInventoryWithZeroQuantity() {
            // Arrange
            Inventory emptyInventory = Inventory.builder()
                    .id("inventory-empty")
                    .quantity(0)
                    .build();
            when(repositoryPort.has(emptyInventory)).thenReturn(true);

            // Act
            boolean result = hasInventoryAdapter.hasInventory(emptyInventory, AUTH_TOKEN);

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should check inventory without ID")
        void shouldCheckInventoryWithoutId() {
            // Arrange
            Inventory noIdInventory = Inventory.builder()
                    .user(User.builder().id("user-123").build())
                    .product(Product.builder().id("product-456").build())
                    .quantity(5)
                    .build();
            when(repositoryPort.has(noIdInventory)).thenReturn(false);

            // Act
            boolean result = hasInventoryAdapter.hasInventory(noIdInventory, AUTH_TOKEN);

            // Assert
            assertThat(result).isFalse();
        }
    }
}

