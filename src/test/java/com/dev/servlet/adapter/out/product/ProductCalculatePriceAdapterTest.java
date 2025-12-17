package com.dev.servlet.adapter.out.product;

import com.dev.servlet.application.port.out.product.ProductRepositoryPort;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.infrastructure.persistence.transfer.IPageable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductCalculatePriceAdapter Tests")
class ProductCalculatePriceAdapterTest {

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @Mock
    private IPageable<?> page;

    @InjectMocks
    private ProductCalculatePriceAdapter productCalculatePriceAdapter;

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id("product-123")
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .owner(User.builder().id("user-123").build())
                .build();
    }

    @Nested
    @DisplayName("Price Calculation Tests")
    class PriceCalculationTests {

        @Test
        @DisplayName("Should calculate total price")
        void shouldCalculateTotalPrice() {
            // Arrange
            BigDecimal expectedPrice = new BigDecimal("299.97");
            when(productRepositoryPort.calculateTotalPriceFor(product)).thenReturn(expectedPrice);

            // Act
            BigDecimal result = productCalculatePriceAdapter.calculateTotalPriceFor(page, product);

            // Assert
            assertThat(result).isEqualByComparingTo(expectedPrice);
            verify(productRepositoryPort).calculateTotalPriceFor(product);
        }

        @Test
        @DisplayName("Should return zero when no products")
        void shouldReturnZeroWhenNoProducts() {
            // Arrange
            when(productRepositoryPort.calculateTotalPriceFor(product)).thenReturn(BigDecimal.ZERO);

            // Act
            BigDecimal result = productCalculatePriceAdapter.calculateTotalPriceFor(page, product);

            // Assert
            assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should delegate to repository")
        void shouldDelegateToRepository() {
            // Arrange
            BigDecimal price = new BigDecimal("100.00");
            when(productRepositoryPort.calculateTotalPriceFor(product)).thenReturn(price);

            // Act
            productCalculatePriceAdapter.calculateTotalPriceFor(page, product);

            // Assert
            verify(productRepositoryPort).calculateTotalPriceFor(product);
        }
    }

    @Nested
    @DisplayName("Different Price Values Tests")
    class DifferentPriceValuesTests {

        @Test
        @DisplayName("Should handle large price values")
        void shouldHandleLargePriceValues() {
            // Arrange
            BigDecimal largePrice = new BigDecimal("999999.99");
            when(productRepositoryPort.calculateTotalPriceFor(product)).thenReturn(largePrice);

            // Act
            BigDecimal result = productCalculatePriceAdapter.calculateTotalPriceFor(page, product);

            // Assert
            assertThat(result).isEqualByComparingTo(largePrice);
        }

        @Test
        @DisplayName("Should handle decimal prices")
        void shouldHandleDecimalPrices() {
            // Arrange
            BigDecimal decimalPrice = new BigDecimal("19.99");
            when(productRepositoryPort.calculateTotalPriceFor(product)).thenReturn(decimalPrice);

            // Act
            BigDecimal result = productCalculatePriceAdapter.calculateTotalPriceFor(page, product);

            // Assert
            assertThat(result).isEqualByComparingTo(decimalPrice);
        }
    }

    @Nested
    @DisplayName("Product Filter Tests")
    class ProductFilterTests {

        @Test
        @DisplayName("Should calculate price for specific product")
        void shouldCalculatePriceForSpecificProduct() {
            // Arrange
            BigDecimal price = new BigDecimal("50.00");
            when(productRepositoryPort.calculateTotalPriceFor(product)).thenReturn(price);

            // Act
            BigDecimal result = productCalculatePriceAdapter.calculateTotalPriceFor(page, product);

            // Assert
            assertThat(result).isNotNull();
            verify(productRepositoryPort).calculateTotalPriceFor(eq(product));
        }
    }
}

