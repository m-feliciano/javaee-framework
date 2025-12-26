package com.dev.servlet.adapter.in.web.controller;

import com.dev.servlet.adapter.in.web.controller.internal.CategoryController;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.application.port.in.category.DeleteCategoryPort;
import com.dev.servlet.application.port.in.category.GetCategoryDetailPort;
import com.dev.servlet.application.port.in.category.ListCategoryPort;
import com.dev.servlet.application.port.in.category.RegisterCategoryPort;
import com.dev.servlet.application.port.in.category.UpdateCategoryPort;
import com.dev.servlet.application.transfer.request.CategoryRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("CategoryController Tests")
class CategoryControllerTest extends BaseControllerTest {

    @Mock
    private GetCategoryDetailPort detailPort;

    @Mock
    private DeleteCategoryPort deletePort;

    @Mock
    private UpdateCategoryPort updatePort;

    @Mock
    private ListCategoryPort listPort;

    @Mock
    private RegisterCategoryPort registerPort;

    @InjectMocks
    private CategoryController categoryController;

    @Override
    protected void setupAdditionalMocks() {
        categoryController.setJwtUtils(authenticationPort);
    }

    @Nested
    @DisplayName("Category Registration Tests")
    class RegistrationTests {

        @Test
        @DisplayName("Should register new category successfully")
        void shouldRegisterCategory() {
            // Arrange
            CategoryRequest request = CategoryRequest.builder()
                    .name("New Category")
                    .build();

            UUID uuid = UUID.randomUUID();

            CategoryResponse expectedResponse = CategoryResponse.builder().id(uuid).build();
            expectedResponse.setName("New Category");
            when(registerPort.register(any(CategoryRequest.class), any())).thenReturn(expectedResponse);

            // Act
            IHttpResponse<Void> response = categoryController.register(request, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.statusCode()).isEqualTo(201);
            assertThat(response.next()).contains(uuid.toString());

            verify(registerPort).register(request, VALID_AUTH_TOKEN);
        }

        @Test
        @DisplayName("Should return 201 status on successful registration")
        void shouldReturn201OnSuccess() {
            // Arrange
            CategoryRequest request = CategoryRequest.builder().name("Test").build();
            CategoryResponse response = CategoryResponse.builder().id(UUID.randomUUID()).build();

            when(registerPort.register(any(), any())).thenReturn(response);

            // Act
            IHttpResponse<Void> result = categoryController.register(request, VALID_AUTH_TOKEN);

            // Assert
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("Category Update Tests")
    class UpdateTests {

        @Test
        @DisplayName("Should update category successfully")
        void shouldUpdateCategory() {
            // Arrange
            UUID uuid = UUID.randomUUID();
            CategoryRequest request = CategoryRequest.builder()
                    .id(uuid)
                    .name("Updated Category")
                    .build();
            CategoryResponse expectedResponse = CategoryResponse.builder().id(uuid).build();
            expectedResponse.setName("Updated Category");
            when(updatePort.update(any(CategoryRequest.class), any())).thenReturn(expectedResponse);

            // Act
            var response = categoryController.update(request, VALID_AUTH_TOKEN);
            // Assert
            assertThat(response).isNotNull();
            assertThat(response.statusCode()).isEqualTo(204);
            assertThat(response.next()).contains(uuid.toString());

            verify(updatePort).update(request, VALID_AUTH_TOKEN);
        }

        @Test
        @DisplayName("Should return 204 status on successful update")
        void shouldReturn204OnUpdate() {
            UUID uuid = UUID.randomUUID();

            // Arrange
            CategoryRequest request = CategoryRequest.builder().id(uuid).build();

            when(updatePort.update(any(), any()))
                    .thenReturn(CategoryResponse.builder().id(uuid).build());

            // Act
            IHttpResponse<Void> response = categoryController.update(request, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
        }
    }

    @Nested
    @DisplayName("Category Deletion Tests")
    class DeletionTests {

        @Test
        @DisplayName("Should delete category successfully")
        void shouldDeleteCategory() {
            // Arrange
            CategoryRequest request = CategoryRequest.builder()
                    .id(UUID.randomUUID())
                    .build();

            doNothing().when(deletePort).delete(any(CategoryRequest.class), eq(VALID_AUTH_TOKEN));

            // Act
            IHttpResponse<Void> response = categoryController.delete(request, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.next()).contains("redirect:");
            assertThat(response.next()).contains("list");

            verify(deletePort).delete(request, VALID_AUTH_TOKEN);
        }

        @Test
        @DisplayName("Should redirect to list after deletion")
        void shouldRedirectToListAfterDeletion() {
            // Arrange
            CategoryRequest request = CategoryRequest.builder().id(UUID.randomUUID()).build();

            doNothing().when(deletePort).delete(any(), any());

            // Act
            IHttpResponse<Void> response = categoryController.delete(request, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response.next()).contains("list");
        }
    }

    @Nested
    @DisplayName("Category Retrieval Tests")
    class RetrievalTests {

        @Test
        @DisplayName("Should list all categories")
        void shouldListAllCategories() {
            // Arrange
            CategoryRequest filter = CategoryRequest.builder().build();

            CategoryResponse cat1 = CategoryResponse.builder().id(UUID.randomUUID()).build();
            cat1.setName("Electronics");

            CategoryResponse cat2 = CategoryResponse.builder().id(UUID.randomUUID()).build();
            cat2.setName("Books");

            CategoryResponse cat3 = CategoryResponse.builder().id(UUID.randomUUID()).build();
            cat3.setName("Clothing");

            List<CategoryResponse> expectedCategories = List.of(cat1, cat2, cat3);

            when(listPort.list(any(CategoryRequest.class), eq(VALID_AUTH_TOKEN)))
                    .thenReturn(expectedCategories);

            // Act
            IHttpResponse<Collection<CategoryResponse>> response =
                    categoryController.list(filter, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).hasSize(3);
            assertThat(response.next()).contains("listCategories");

            verify(listPort).list(filter, VALID_AUTH_TOKEN);
        }

        @Test
        @DisplayName("Should retrieve category details by ID")
        void shouldGetCategoryDetails() {
            // Arrange
            UUID uuid = UUID.randomUUID();
            CategoryRequest request = CategoryRequest.builder().id(uuid).build();

            CategoryResponse expectedCategory = CategoryResponse.builder().id(uuid).build();
            expectedCategory.setName("Electronics");
            expectedCategory.setStatus("ACTIVE");

            when(detailPort.get(any(CategoryRequest.class), eq(VALID_AUTH_TOKEN)))
                    .thenReturn(expectedCategory);

            // Act
            IHttpResponse<CategoryResponse> response = categoryController.details(request, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isEqualTo(expectedCategory);
            assertThat(response.next()).contains("formUpdateCategory");

            verify(detailPort).get(request, VALID_AUTH_TOKEN);
        }

        @Test
        @DisplayName("Should get category detail for display")
        void shouldGetCategoryDetail() {
            UUID uuid = UUID.randomUUID();
            // Arrange
            CategoryRequest request = CategoryRequest.builder().id(uuid).build();

            CategoryResponse expectedCategory = CategoryResponse.builder().id(uuid).build();
            expectedCategory.setName("Books");

            when(detailPort.get(any(CategoryRequest.class), eq(VALID_AUTH_TOKEN)))
                    .thenReturn(expectedCategory);

            // Act
            IHttpResponse<CategoryResponse> response =
                    categoryController.getCategoryDetail(request, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isEqualTo(expectedCategory);
            assertThat(response.next()).contains("formListCategory");

            verify(detailPort).get(request, VALID_AUTH_TOKEN);
        }
    }

    @Nested
    @DisplayName("Forward Navigation Tests")
    class ForwardTests {

        @Test
        @DisplayName("Should forward to category registration form")
        void shouldForwardToRegistrationForm() {
            // Act
            IHttpResponse<Void> response = categoryController.forwardRegister();

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.next()).contains("forward:");
            assertThat(response.next()).contains("formCreateCategory");
        }

        @Test
        @DisplayName("Should not require authentication parameters for form forward")
        void shouldNotRequireAuthForFormForward() {
            // Act
            IHttpResponse<Void> response = categoryController.forwardRegister();

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isNull();
        }
    }

    @Nested
    @DisplayName("Controller Implementation Tests")
    class ImplementationTests {

        @Test
        @DisplayName("Should implement CategoryControllerApi interface")
        void shouldImplementInterface() {
            assertThat(categoryController).isInstanceOf(CategoryControllerApi.class);
        }

        @Test
        @DisplayName("Should have all required dependencies injected")
        void shouldHaveAllDependencies() {
            assertThat(categoryController).extracting("detailPort").isNotNull();
            assertThat(categoryController).extracting("deletePort").isNotNull();
            assertThat(categoryController).extracting("updatePort").isNotNull();
            assertThat(categoryController).extracting("listPort").isNotNull();
            assertThat(categoryController).extracting("registerPort").isNotNull();
        }

        @Test
        @DisplayName("Should handle all CRUD operations")
        void shouldHandleCrudOperations() {
            // Arrange
            UUID uuid = UUID.randomUUID();
            CategoryRequest request = CategoryRequest.builder()
                    .id(uuid)
                    .name("Test Category")
                    .build();

            CategoryResponse response = CategoryResponse.builder().id(uuid).build();
            response.setName("Test Category");

            when(registerPort.register(any(), any())).thenReturn(response);
            when(updatePort.update(any(), any())).thenReturn(response);
            when(detailPort.get(any(), any())).thenReturn(response);
            when(listPort.list(any(), any())).thenReturn(List.of(response));
            doNothing().when(deletePort).delete(any(), any());

            // Act & Assert - All operations should work
            assertThat(categoryController.register(request, VALID_AUTH_TOKEN)).isNotNull();
            assertThat(categoryController.update(request, VALID_AUTH_TOKEN)).isNotNull();
            assertThat(categoryController.details(request, VALID_AUTH_TOKEN)).isNotNull();
            assertThat(categoryController.getCategoryDetail(request, VALID_AUTH_TOKEN)).isNotNull();
            assertThat(categoryController.list(request, VALID_AUTH_TOKEN)).isNotNull();
            assertThat(categoryController.delete(request, VALID_AUTH_TOKEN)).isNotNull();
        }
    }
}
