package com.servletstack.adapter.in.web.controller;

import com.servletstack.adapter.in.web.controller.internal.InventoryController;
import com.servletstack.adapter.in.web.dto.IHttpResponse;
import com.servletstack.adapter.in.web.dto.IServletResponse;
import com.servletstack.application.mapper.InventoryMapper;
import com.servletstack.application.mapper.Mapper;
import com.servletstack.application.port.in.category.ListCategoryUseCase;
import com.servletstack.application.port.in.product.ProductDetailUserCase;
import com.servletstack.application.port.in.stock.DeleteInventoryUseCase;
import com.servletstack.application.port.in.stock.GetInventoryDetailUseCase;
import com.servletstack.application.port.in.stock.ListInventoryUseCase;
import com.servletstack.application.port.in.stock.RegisterInventoryUseCase;
import com.servletstack.application.port.in.stock.UpdateInventoryUseCase;
import com.servletstack.application.transfer.request.InventoryCreateRequest;
import com.servletstack.application.transfer.request.InventoryRequest;
import com.servletstack.application.transfer.request.ProductRequest;
import com.servletstack.application.transfer.response.CategoryResponse;
import com.servletstack.application.transfer.response.InventoryResponse;
import com.servletstack.application.transfer.response.ProductResponse;
import com.servletstack.domain.entity.Inventory;
import com.servletstack.domain.entity.Product;
import com.servletstack.infrastructure.persistence.transfer.IPageRequest;
import com.servletstack.infrastructure.persistence.transfer.IPageable;
import com.servletstack.infrastructure.persistence.transfer.internal.PageRequest;
import com.servletstack.infrastructure.persistence.transfer.internal.PageResponse;
import com.servletstack.shared.vo.Query;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    private ListCategoryUseCase listCategoryUseCase;
    @Mock
    private ListInventoryUseCase listInventoryUseCase;
    @Mock
    private UpdateInventoryUseCase updateInventoryUseCase;
    @Mock
    private RegisterInventoryUseCase registerInventoryUseCase;
    @Mock
    private DeleteInventoryUseCase deleteInventoryUseCase;
    @Mock
    private GetInventoryDetailUseCase inventoryDetailUseCase;
    @Mock
    private ProductDetailUserCase productDetailUserCase;

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
                    UUID.randomUUID()
            );

            UUID uuid = UUID.randomUUID();
            InventoryResponse expectedResponse = new InventoryResponse(uuid);
            expectedResponse.setQuantity(100);
            expectedResponse.setDescription("Initial stock");

            when(registerInventoryUseCase.register(any(InventoryCreateRequest.class), eq(VALID_AUTH_TOKEN)))
                    .thenReturn(expectedResponse);

            // Act
            IHttpResponse<Void> response = inventoryController.create(request, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.next()).contains("redirect:");
            assertThat(response.next()).contains(uuid.toString());

            verify(registerInventoryUseCase).register(request, VALID_AUTH_TOKEN);
        }
    }

    @Nested
    @DisplayName("Inventory Update Tests")
    class UpdateTests {

        @Test
        @DisplayName("Should update inventory successfully")
        void shouldUpdateInventory() {
            // Arrange
            UUID uuid = UUID.randomUUID();
            InventoryRequest request = InventoryRequest.builder()
                    .id(uuid)
                    .quantity(150)
                    .build();

            InventoryResponse expectedResponse = new InventoryResponse(uuid);
            expectedResponse.setQuantity(150);

            when(updateInventoryUseCase.update(any(InventoryRequest.class), eq(VALID_AUTH_TOKEN)))
                    .thenReturn(expectedResponse);

            // Act
            IHttpResponse<Void> response = inventoryController.update(request, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.next()).contains("redirect:");
            assertThat(response.next()).contains(uuid.toString());

            verify(updateInventoryUseCase).update(request, VALID_AUTH_TOKEN);
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
                    .id(UUID.randomUUID())
                    .build();

            doNothing().when(deleteInventoryUseCase).delete(any(InventoryRequest.class), eq(VALID_AUTH_TOKEN));

            // Act
            IHttpResponse<Void> response = inventoryController.delete(request, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.next()).contains("redirect:");
            assertThat(response.next()).contains("list");

            verify(deleteInventoryUseCase).delete(request, VALID_AUTH_TOKEN);
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

            InventoryResponse inv1 = new InventoryResponse(UUID.randomUUID());
            inv1.setQuantity(100);

            InventoryResponse inv2 = new InventoryResponse(UUID.randomUUID());
            inv2.setQuantity(50);

            List<InventoryResponse> inventories = List.of(inv1, inv2);

            CategoryResponse cat = CategoryResponse.builder().id(UUID.randomUUID()).build();
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

            when(listInventoryUseCase.getAllPageable(any(PageRequest.class), eq(VALID_AUTH_TOKEN), any(Mapper.class)))
                    .thenReturn((PageResponse) pageResponse);

            when(listCategoryUseCase.list(any(), eq(VALID_AUTH_TOKEN))).thenReturn(categories);

            // Act
            IServletResponse response = inventoryController.list(pageRequest, filter, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isNotNull();
            assertThat(response.body()).hasSize(2); // items and categories

            verify(listInventoryUseCase).getAllPageable(any(PageRequest.class), eq(VALID_AUTH_TOKEN), any(Mapper.class));
            verify(listCategoryUseCase).list(null, VALID_AUTH_TOKEN);
        }

        @Test
        @DisplayName("Should retrieve inventory detail by ID")
        void shouldGetInventoryDetail() {
            // Arrange
            UUID uuid = UUID.randomUUID();
            InventoryRequest request = InventoryRequest.builder().id(uuid).build();

            InventoryResponse expectedInventory = new InventoryResponse(uuid);
            expectedInventory.setQuantity(100);

            when(inventoryDetailUseCase.get(any(InventoryRequest.class), eq(VALID_AUTH_TOKEN)))
                    .thenReturn(expectedInventory);

            // Act
            IHttpResponse<InventoryResponse> response = inventoryController.findById(request, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isEqualTo(expectedInventory);

            verify(inventoryDetailUseCase).get(request, VALID_AUTH_TOKEN);
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
            UUID uuid = UUID.randomUUID();
            Map<String, String> params = new HashMap<>();
            params.put("productId", "product-123");
            Query query = Query.builder().parameters(params).build();

            List<InventoryResponse> searchResults = List.of(new InventoryResponse(uuid));

            InventoryRequest filter = InventoryRequest.builder().build();

            when(inventoryMapper.queryToInventory(any(Query.class))).thenReturn(filter);
            when(inventoryMapper.toResponse(any(Inventory.class)))
                    .thenReturn(new InventoryResponse(uuid));

            when(inventoryMapper.toInventory(any()))
                    .thenReturn(new Inventory(
                            new Product(UUID.randomUUID()),
                            100,
                            "Sample inventory")
                    );
            when(listCategoryUseCase.list(any(), eq(VALID_AUTH_TOKEN))).thenReturn(List.of());

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

            when(listInventoryUseCase.getAllPageable(any(), eq(VALID_AUTH_TOKEN), any()))
                    .thenReturn((PageResponse) pageableResponse);

            // Act
            IServletResponse response = inventoryController.search(query, pageRequest, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isNotNull();

            verify(inventoryMapper).toInventory(any());
            verify(inventoryMapper).queryToInventory(any(Query.class));
            verify(listCategoryUseCase).list(null, VALID_AUTH_TOKEN);
            verify(listInventoryUseCase).getAllPageable(any(PageRequest.class), eq(VALID_AUTH_TOKEN), any(Mapper.class));
        }
    }

    @Nested
    @DisplayName("Forward Navigation Tests")
    class ForwardTests {

        @Test
        @DisplayName("Should forward to registration form with product details")
        void shouldForwardToRegistrationForm() {
            // Arrange
            UUID uuid = UUID.randomUUID();

            Map<String, String> params = new HashMap<>();
            params.put("productId", uuid.toString());
            Query query = Query.builder().parameters(params).build();

            ProductResponse product = new ProductResponse(uuid);
            product.setName("Test Product");

            when(productDetailUserCase.get(any(ProductRequest.class), eq(VALID_AUTH_TOKEN)))
                    .thenReturn(product);

            // Act
            IHttpResponse<ProductResponse> response = inventoryController.forwardRegister(query, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isEqualTo(product);

            verify(productDetailUserCase).get(any(ProductRequest.class), eq(VALID_AUTH_TOKEN));
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

