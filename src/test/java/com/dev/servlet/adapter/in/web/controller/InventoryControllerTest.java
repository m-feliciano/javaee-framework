package com.dev.servlet.adapter.in.web.controller;

import com.dev.servlet.adapter.in.web.controller.internal.InventoryController;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.adapter.in.web.dto.IServletResponse;
import com.dev.servlet.application.mapper.InventoryMapper;
import com.dev.servlet.application.mapper.Mapper;
import com.dev.servlet.application.port.in.category.ListCategoryPort;
import com.dev.servlet.application.port.in.product.ProductDetailPort;
import com.dev.servlet.application.port.in.stock.DeleteInventoryPort;
import com.dev.servlet.application.port.in.stock.GetInventoryDetailPort;
import com.dev.servlet.application.port.in.stock.ListInventoryPort;
import com.dev.servlet.application.port.in.stock.RegisterInventoryPort;
import com.dev.servlet.application.port.in.stock.UpdateInventoryPort;
import com.dev.servlet.application.transfer.request.InventoryCreateRequest;
import com.dev.servlet.application.transfer.request.InventoryRequest;
import com.dev.servlet.application.transfer.request.ProductRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import com.dev.servlet.application.transfer.response.InventoryResponse;
import com.dev.servlet.application.transfer.response.ProductResponse;
import com.dev.servlet.domain.entity.Inventory;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.infrastructure.persistence.transfer.IPageable;
import com.dev.servlet.infrastructure.persistence.transfer.internal.PageRequest;
import com.dev.servlet.infrastructure.persistence.transfer.internal.PageResponse;
import com.dev.servlet.shared.vo.Query;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("InventoryController Tests")
class InventoryControllerTest extends BaseControllerTest {

    @Mock
    private InventoryMapper inventoryMapper;
    @Mock
    private ListCategoryPort listCategoryPort;
    @Mock
    private ListInventoryPort listInventoryPort;
    @Mock
    private UpdateInventoryPort updateInventoryPort;
    @Mock
    private RegisterInventoryPort registerInventoryPort;
    @Mock
    private DeleteInventoryPort deleteInventoryPort;
    @Mock
    private GetInventoryDetailPort inventoryDetailPort;
    @Mock
    private ProductDetailPort productDetailPort;

    @InjectMocks
    private InventoryController inventoryController;

    @Override
    protected void setupAdditionalMocks() {
        inventoryController.setJwtUtils(authenticationPort);
    }

    @Nested
    @DisplayName("Inventory Registration Tests")
    class RegistrationTests {

        @Test
        @DisplayName("Should register new inventory item successfully")
        void shouldRegisterInventoryItem() {
            // Arrange
            InventoryCreateRequest request = new InventoryCreateRequest(
                    100,
                    "Initial stock",
                    "product-123"
            );

            InventoryResponse expectedResponse = new InventoryResponse("inventory-123");
            expectedResponse.setQuantity(100);
            expectedResponse.setDescription("Initial stock");

            when(registerInventoryPort.register(any(InventoryCreateRequest.class), eq(VALID_AUTH_TOKEN)))
                    .thenReturn(expectedResponse);

            // Act
            IHttpResponse<Void> response = inventoryController.create(request, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.next()).contains("redirect:");
            assertThat(response.next()).contains("inventory-123");

            verify(registerInventoryPort).register(request, VALID_AUTH_TOKEN);
        }
    }

    @Nested
    @DisplayName("Inventory Update Tests")
    class UpdateTests {

        @Test
        @DisplayName("Should update inventory successfully")
        void shouldUpdateInventory() {
            // Arrange
            InventoryRequest request = InventoryRequest.builder()
                    .id("inventory-123")
                    .quantity(150)
                    .build();

            InventoryResponse expectedResponse = new InventoryResponse("inventory-123");
            expectedResponse.setQuantity(150);

            when(updateInventoryPort.update(any(InventoryRequest.class), eq(VALID_AUTH_TOKEN)))
                    .thenReturn(expectedResponse);

            // Act
            IHttpResponse<Void> response = inventoryController.update(request, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.next()).contains("redirect:");
            assertThat(response.next()).contains("inventory-123");

            verify(updateInventoryPort).update(request, VALID_AUTH_TOKEN);
        }
    }

    @Nested
    @DisplayName("Inventory Deletion Tests")
    class DeletionTests {

        @Test
        @DisplayName("Should delete inventory item successfully")
        void shouldDeleteInventory() {
            // Arrange
            InventoryRequest request = InventoryRequest.builder()
                    .id("inventory-to-delete")
                    .build();

            doNothing().when(deleteInventoryPort).delete(any(InventoryRequest.class), eq(VALID_AUTH_TOKEN));

            // Act
            IHttpResponse<Void> response = inventoryController.delete(request, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.next()).contains("redirect:");
            assertThat(response.next()).contains("list");

            verify(deleteInventoryPort).delete(request, VALID_AUTH_TOKEN);
        }
    }

    @Nested
    @DisplayName("Inventory Retrieval Tests")
    class RetrievalTests {

        @Test
        @DisplayName("Should list all inventory items with categories")
        @SuppressWarnings("all")
        void shouldListAllInventoryItems() {
            // Arrange
            InventoryRequest filter = InventoryRequest.builder().build();

            InventoryResponse inv1 = new InventoryResponse("inv-1");
            inv1.setQuantity(100);

            InventoryResponse inv2 = new InventoryResponse("inv-2");
            inv2.setQuantity(50);

            List<InventoryResponse> inventories = List.of(inv1, inv2);

            CategoryResponse cat = new CategoryResponse("cat-1");
            cat.setName("Electronics");
            List<CategoryResponse> categories = List.of(cat);

            IPageRequest pageRequest = PageRequest.builder()
                    .initialPage(0)
                    .pageSize(10)
                    .filter(filter)
                    .build();

            IPageable<InventoryResponse> pageResponse = PageResponse.<InventoryResponse>builder()
                    .content(inventories)
                    .totalElements(2)
                    .currentPage(0)
                    .pageSize(10)
                    .build();

            when(listInventoryPort.getAllPageable(any(PageRequest.class), eq(VALID_AUTH_TOKEN), any(Mapper.class)))
                    .thenReturn((PageResponse) pageResponse);

            when(listCategoryPort.list(any(), eq(VALID_AUTH_TOKEN))).thenReturn(categories);

            // Act
            IServletResponse response = inventoryController.list(pageRequest, filter, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isNotNull();
            assertThat(response.body()).hasSize(2); // items and categories

            verify(listInventoryPort).getAllPageable(any(PageRequest.class), eq(VALID_AUTH_TOKEN), any(Mapper.class));
            verify(listCategoryPort).list(null, VALID_AUTH_TOKEN);
        }

        @Test
        @DisplayName("Should retrieve inventory detail by ID")
        void shouldGetInventoryDetail() {
            // Arrange
            InventoryRequest request = InventoryRequest.builder().id("inventory-123").build();

            InventoryResponse expectedInventory = new InventoryResponse("inventory-123");
            expectedInventory.setQuantity(100);

            when(inventoryDetailPort.get(any(InventoryRequest.class), eq(VALID_AUTH_TOKEN)))
                    .thenReturn(expectedInventory);

            // Act
            IHttpResponse<InventoryResponse> response = inventoryController.findById(request, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isEqualTo(expectedInventory);

            verify(inventoryDetailPort).get(request, VALID_AUTH_TOKEN);
        }
    }

    @Nested
    @DisplayName("Inventory Search Tests")
    class SearchTests {

        @Test
        @DisplayName("Should search inventory with query parameters")
        @SuppressWarnings("all")
        void shouldSearchInventory() {
            // Arrange
            Map<String, String> params = new HashMap<>();
            params.put("productId", "product-123");
            Query query = Query.builder().parameters(params).build();

            List<InventoryResponse> searchResults = List.of(new InventoryResponse("inv-1"));

            InventoryRequest filter = InventoryRequest.builder().build();

            when(inventoryMapper.queryToInventory(any(Query.class))).thenReturn(filter);
            when(inventoryMapper.toResponse(any(Inventory.class)))
                    .thenReturn(new InventoryResponse("inv-1"));

            when(inventoryMapper.toInventory(any()))
                    .thenReturn(new Inventory(
                            new Product("product-123"),
                            100,
                            "Sample inventory")
                    );
            when(listCategoryPort.list(any(), eq(VALID_AUTH_TOKEN))).thenReturn(List.of());

            IPageRequest pageRequest = PageRequest.builder()
                    .filter(filter)
                    .initialPage(0)
                    .pageSize(10)
                    .build();

            IPageable<InventoryResponse> pageableResponse = PageResponse.<InventoryResponse>builder()
                    .content(searchResults)
                    .totalElements(1)
                    .currentPage(0)
                    .pageSize(10)
                    .build();

            when(listInventoryPort.getAllPageable(any(), eq(VALID_AUTH_TOKEN), any()))
                    .thenReturn((PageResponse) pageableResponse);

            // Act
            IServletResponse response = inventoryController.search(query, pageRequest, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isNotNull();

            verify(inventoryMapper).toInventory(any());
            verify(inventoryMapper).queryToInventory(any(Query.class));
            verify(listCategoryPort).list(null, VALID_AUTH_TOKEN);
            verify(listInventoryPort).getAllPageable(any(PageRequest.class), eq(VALID_AUTH_TOKEN), any(Mapper.class));
        }
    }

    @Nested
    @DisplayName("Forward Navigation Tests")
    class ForwardTests {

        @Test
        @DisplayName("Should forward to registration form with product details")
        void shouldForwardToRegistrationForm() {
            // Arrange
            Map<String, String> params = new HashMap<>();
            params.put("productId", "product-123");
            Query query = Query.builder().parameters(params).build();

            ProductResponse product = new ProductResponse("product-123");
            product.setName("Test Product");

            when(productDetailPort.get(any(ProductRequest.class), eq(VALID_AUTH_TOKEN)))
                    .thenReturn(product);

            // Act
            IHttpResponse<ProductResponse> response = inventoryController.forwardRegister(query, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isEqualTo(product);

            verify(productDetailPort).get(any(ProductRequest.class), eq(VALID_AUTH_TOKEN));
        }

        @Test
        @DisplayName("Should handle forward without product ID")
        void shouldHandleForwardWithoutProductId() {
            // Arrange
            Query emptyQuery = Query.builder().parameters(new HashMap<>()).build();

            // Act
            IHttpResponse<ProductResponse> response = inventoryController.forwardRegister(emptyQuery, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isNull();
        }
    }

    @Nested
    @DisplayName("Controller Implementation Tests")
    class ImplementationTests {

        @Test
        @DisplayName("Should implement InventoryControllerApi interface")
        void shouldImplementInterface() {
            assertThat(inventoryController).isInstanceOf(InventoryControllerApi.class);
        }
    }
}

