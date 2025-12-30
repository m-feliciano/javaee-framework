package com.servletstack.adapter.out.inventory;

import com.servletstack.application.port.out.inventory.InventoryRepositoryPort;
import com.servletstack.domain.entity.Inventory;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("HasInventoryAdapter Tests")
class HasInventoryAdapterTest {

    private static final String AUTH_TOKEN = "Bearer valid.token";
    private static final UUID INVENTORY_ID = UUID.fromString("323e4567-e89b-12d3-a456-426614174000");
    private static final UUID USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID PRODUCT_ID = UUID.fromString("223e4567-e89b-12d3-a456-426614174000");

    @Mock
    private InventoryRepositoryPort repositoryPort;
    @InjectMocks
    private HasInventoryAdapter hasInventoryAdapter;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = Inventory.builder()
                .id(INVENTORY_ID)
                .user(User.builder().id(USER_ID).build())
                .product(Product.builder().id(PRODUCT_ID).build())
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
                    .id(UUID.fromString("333e4567-e89b-12d3-a456-426614174000"))
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
                    .user(User.builder().id(USER_ID).build())
                    .product(Product.builder().id(PRODUCT_ID).build())
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
