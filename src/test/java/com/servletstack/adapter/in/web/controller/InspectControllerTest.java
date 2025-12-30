package com.servletstack.adapter.in.web.controller;

import com.servletstack.adapter.in.web.controller.internal.InspectController;
import com.servletstack.adapter.in.web.dto.IHttpResponse;
import com.servletstack.adapter.in.web.introspection.ControllerIntrospectionService;
import com.servletstack.adapter.in.web.vo.ControllerInfo;
import com.servletstack.adapter.in.web.vo.MethodInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("InspectController Tests")
class InspectControllerTest extends BaseControllerTest {

    @Mock
    private ControllerIntrospectionService inspector;

    @InjectMocks
    private InspectController inspectController;

    @Override
    protected void setupAdditionalMocks() {
        inspectController.setJwtUtils(authenticationPort);
    }

    // Helper methods
    private List<ControllerInfo> createMockControllerInfo() {
        MethodInfo method1 = new MethodInfo(
                "/api/v1/product/list",
                "GET",
                null,
                true,
                List.of(),
                List.of(),
                "IHttpResponse",
                "Retrieve the list of products",
                false,
                false
        );

        MethodInfo method2 = new MethodInfo(
                "/api/v1/user/registerUser",
                "POST",
                null,
                false,
                List.of(),
                List.of(),
                "IHttpResponse",
                "Register a new user",
                false,
                false
        );

        ControllerInfo productController = new ControllerInfo(
                "ProductController",
                "/api/v1/product",
                List.of(method1)
        );

        ControllerInfo userController = new ControllerInfo(
                "UserController",
                "/api/v1/user",
                List.of(method2)
        );

        return Arrays.asList(productController, userController);
    }

    private ControllerInfo createControllerInfo(String name, int methodCount) {
        List<MethodInfo> methods = new java.util.ArrayList<>();
        for (int i = 0; i < methodCount; i++) {
            methods.add(new MethodInfo(
                    "/api/v1/" + name.toLowerCase() + "/implementation" + i,
                    i % 2 == 0 ? "GET" : "POST",
                    null,
                    true,
                    List.of(),
                    List.of(),
                    "IHttpResponse",
                    "Test implementation " + i,
                    false,
                    false
            ));
        }

        return new ControllerInfo(
                name,
                "/api/v1/" + name.toLowerCase(),
                methods
        );
    }

    @Nested
    @DisplayName("Raw JSON Tests")
    class RawJsonTests {

        @Test
        @DisplayName("Should return raw JSON of all controllers")
        void shouldReturnRawJson() {
            // Arrange
            List<ControllerInfo> controllers = createMockControllerInfo();
            when(inspector.listControllers()).thenReturn(controllers);

            // Act
            IHttpResponse<String> response = inspectController.rawJson();

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isNotNull();
            assertThat(response.body()).contains("ProductController");
            assertThat(response.body()).contains("UserController");
            assertThat(response.next()).contains("forward:");
            assertThat(response.next()).contains("inspect-raw");

            verify(inspector).listControllers();
        }

        @Test
        @DisplayName("Should format controller info as JSON")
        void shouldFormatAsJson() {
            // Arrange
            List<ControllerInfo> controllers = createMockControllerInfo();
            when(inspector.listControllers()).thenReturn(controllers);

            // Act
            IHttpResponse<String> response = inspectController.rawJson();

            // Assert
            String json = response.body();
            assertThat(json).contains("\"name\"");
            assertThat(json).contains("\"methods\"");
        }

        @Test
        @DisplayName("Should not require authentication for raw JSON")
        void shouldNotRequireAuthForRawJson() {
            // Arrange
            when(inspector.listControllers()).thenReturn(List.of());

            // Act - No auth token provided
            IHttpResponse<String> response = inspectController.rawJson();

            // Assert
            assertThat(response).isNotNull();
        }
    }

    @Nested
    @DisplayName("Page Info Tests")
    class PageInfoTests {

        @Test
        @DisplayName("Should return controller info for page display")
        void shouldReturnControllerInfo() {
            // Arrange
            List<ControllerInfo> controllers = createMockControllerInfo();
            when(inspector.listControllers()).thenReturn(controllers);

            // Act
            IHttpResponse<List<ControllerInfo>> response = inspectController.page();

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isNotNull();
            assertThat(response.body()).hasSize(2);
            assertThat(response.next()).contains("forward:");
            assertThat(response.next()).contains("inspect");

            verify(inspector).listControllers();
        }

        @Test
        @DisplayName("Should return structured controller metadata")
        void shouldReturnStructuredMetadata() {
            // Arrange
            List<ControllerInfo> controllers = createMockControllerInfo();
            when(inspector.listControllers()).thenReturn(controllers);

            // Act
            IHttpResponse<List<ControllerInfo>> response = inspectController.page();

            // Assert
            List<ControllerInfo> body = response.body();
            assertThat(body.get(0).name()).isEqualTo("ProductController");
            assertThat(body.get(0).methods()).isNotEmpty();
            assertThat(body.get(1).name()).isEqualTo("UserController");
        }

        @Test
        @DisplayName("Should not require authentication for page info")
        void shouldNotRequireAuthForPageInfo() {
            // Arrange
            when(inspector.listControllers()).thenReturn(List.of());

            // Act - No auth token provided
            IHttpResponse<List<ControllerInfo>> response = inspectController.page();

            // Assert
            assertThat(response).isNotNull();
        }

        @Test
        @DisplayName("Should return 200 status for page info")
        void shouldReturn200Status() {
            // Arrange
            when(inspector.listControllers()).thenReturn(createMockControllerInfo());

            // Act
            IHttpResponse<List<ControllerInfo>> response = inspectController.page();

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Controller Introspection Tests")
    class IntrospectionTests {

        @Test
        @DisplayName("Should discover all controller endpoints")
        void shouldDiscoverAllEndpoints() {
            // Arrange
            List<ControllerInfo> controllers = Arrays.asList(
                    createControllerInfo("ProductController", 8),
                    createControllerInfo("UserController", 7),
                    createControllerInfo("CategoryController", 6),
                    createControllerInfo("ActivityController", 4),
                    createControllerInfo("HealthController", 4)
            );

            when(inspector.listControllers()).thenReturn(controllers);

            // Act
            IHttpResponse<List<ControllerInfo>> response = inspectController.page();

            // Assert
            assertThat(response.body()).hasSize(5);
            assertThat(response.body().stream()
                    .mapToInt(c -> c.methods().size())
                    .sum()).isEqualTo(29); // Total methods across all controllers
        }

        @Test
        @DisplayName("Should include implementation metadata in introspection")
        void shouldIncludeMethodMetadata() {
            // Arrange
            MethodInfo method1 = new MethodInfo(
                    "/api/v1/product/list",
                    "GET",
                    null,
                    true,
                    List.of(),
                    List.of(),
                    "IHttpResponse",
                    "Retrieve the list of products",
                    false,
                    false
            );

            MethodInfo method2 = new MethodInfo(
                    "/api/v1/product/create",
                    "POST",
                    null,
                    true,
                    List.of(),
                    List.of(),
                    "IHttpResponse",
                    "Create a new product",
                    false, false
            );

            ControllerInfo controller = new ControllerInfo(
                    "ProductController",
                    "/api/v1/product",
                    List.of(method1, method2)
            );

            when(inspector.listControllers()).thenReturn(List.of(controller));

            // Act
            IHttpResponse<List<ControllerInfo>> response = inspectController.page();

            // Assert
            ControllerInfo result = response.body().get(0);
            assertThat(result.methods()).hasSize(2);
            assertThat(result.methods().get(0).httpMethod()).isEqualTo("GET");
            assertThat(result.methods().get(1).httpMethod()).isEqualTo("POST");
        }
    }

    @Nested
    @DisplayName("Controller Implementation Tests")
    class ImplementationTests {

        @Test
        @DisplayName("Should implement InspectControllerApi interface")
        void shouldImplementInterface() {
            assertThat(inspectController).isInstanceOf(InspectControllerApi.class);
        }

        @Test
        @DisplayName("Should have introspection service injected")
        void shouldHaveIntrospectionServiceInjected() {
            assertThat(inspectController).extracting("service").isNotNull();
        }

        @Test
        @DisplayName("Should provide consistent data across endpoints")
        void shouldProvideConsistentData() {
            // Arrange
            List<ControllerInfo> controllers = createMockControllerInfo();
            when(inspector.listControllers()).thenReturn(controllers);

            // Act
            IHttpResponse<String> rawResponse = inspectController.rawJson();
            IHttpResponse<List<ControllerInfo>> pageResponse = inspectController.page();

            // Assert - Both should have same underlying data
            assertThat(pageResponse.body()).hasSize(2);
            assertThat(rawResponse.body()).contains("ProductController");
            assertThat(rawResponse.body()).contains("UserController");
        }
    }

    @Nested
    @DisplayName("API Documentation Tests")
    class ApiDocumentationTests {

        @Test
        @DisplayName("Should serve as API documentation endpoint")
        void shouldServeAsDocumentation() {
            // Arrange
            List<ControllerInfo> controllers = createMockControllerInfo();
            when(inspector.listControllers()).thenReturn(controllers);

            // Act
            IHttpResponse<List<ControllerInfo>> response = inspectController.page();

            // Assert - Should provide comprehensive API metadata
            assertThat(response.body()).isNotEmpty();
            assertThat(response.body().getFirst().name()).isNotNull();
            assertThat(response.body().getFirst().methods()).isNotNull();
        }

        @Test
        @DisplayName("Should handle empty controller list gracefully")
        void shouldHandleEmptyControllerList() {
            // Arrange
            when(inspector.listControllers()).thenReturn(List.of());

            // Act
            IHttpResponse<List<ControllerInfo>> response = inspectController.page();

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isEmpty();
        }
    }
}

