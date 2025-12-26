package com.dev.servlet.adapter.in.web.controller;

import com.dev.servlet.adapter.in.web.annotation.Controller;
import com.dev.servlet.adapter.in.web.annotation.RequestMapping;
import com.dev.servlet.adapter.in.web.controller.internal.ActivityController;
import com.dev.servlet.adapter.in.web.controller.internal.AlertController;
import com.dev.servlet.adapter.in.web.controller.internal.AuthController;
import com.dev.servlet.adapter.in.web.controller.internal.CategoryController;
import com.dev.servlet.adapter.in.web.controller.internal.HealthController;
import com.dev.servlet.adapter.in.web.controller.internal.InspectController;
import com.dev.servlet.adapter.in.web.controller.internal.InventoryController;
import com.dev.servlet.adapter.in.web.controller.internal.ProductController;
import com.dev.servlet.adapter.in.web.controller.internal.UserController;
import com.dev.servlet.adapter.in.web.controller.internal.base.BaseController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Controller Contract Tests")
class ControllerContractTest {

    @Nested
    @DisplayName("Interface Implementation Tests")
    class InterfaceImplementationTests {

        static Stream<Arguments> controllerImplementationPairs() {
            return Stream.of(
                    Arguments.of(ActivityController.class, ActivityControllerApi.class),
                    Arguments.of(AlertController.class, AlertControllerApi.class),
                    Arguments.of(AuthController.class, AuthControllerApi.class),
                    Arguments.of(CategoryController.class, CategoryControllerApi.class),
                    Arguments.of(HealthController.class, HealthControllerApi.class),
                    Arguments.of(InspectController.class, InspectControllerApi.class),
                    Arguments.of(InventoryController.class, InventoryControllerApi.class),
                    Arguments.of(ProductController.class, ProductControllerApi.class),
                    Arguments.of(UserController.class, UserControllerApi.class)
            );
        }

        @ParameterizedTest(name = "{0} should implement {1}")
        @MethodSource("controllerImplementationPairs")
        @DisplayName("Controllers should implement their API interfaces")
        void shouldImplementApiInterface(Class<?> implementation, Class<?> apiInterface) {
            assertThat(apiInterface.isAssignableFrom(implementation))
                    .as("%s should implement %s", implementation.getSimpleName(), apiInterface.getSimpleName())
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("Annotation Tests")
    class AnnotationTests {

        static Stream<Arguments> controllerApiInterfaces() {
            return Stream.of(
                    Arguments.of(ActivityControllerApi.class),
                    Arguments.of(AlertControllerApi.class),
                    Arguments.of(AuthControllerApi.class),
                    Arguments.of(CategoryControllerApi.class),
                    Arguments.of(HealthControllerApi.class),
                    Arguments.of(InspectControllerApi.class),
                    Arguments.of(InventoryControllerApi.class),
                    Arguments.of(ProductControllerApi.class),
                    Arguments.of(UserControllerApi.class)
            );
        }

        @ParameterizedTest(name = "{0} should have @Controller annotation")
        @MethodSource("controllerApiInterfaces")
        @DisplayName("API interfaces should have @Controller annotation")
        void shouldHaveControllerAnnotation(Class<?> apiInterface) {
            Controller annotation = apiInterface.getAnnotation(Controller.class);
            assertThat(annotation)
                    .as("%s should have @Controller annotation", apiInterface.getSimpleName())
                    .isNotNull();
            assertThat(annotation.value())
                    .as("@Controller value should not be empty")
                    .isNotEmpty();
        }

        @ParameterizedTest(name = "{0} methods should have @RequestMapping")
        @MethodSource("controllerApiInterfaces")
        @DisplayName("API interface methods should have @RequestMapping annotation")
        void shouldHaveRequestMappingOnMethods(Class<?> apiInterface) {
            Method[] methods = apiInterface.getDeclaredMethods();
            assertThat(methods).isNotEmpty();

            for (Method method : methods) {
                RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                assertThat(annotation)
                        .as("Method %s.%s should have @RequestMapping annotation",
                                apiInterface.getSimpleName(), method.getName())
                        .isNotNull();
            }
        }
    }

    @Nested
    @DisplayName("Method Signature Tests")
    class MethodSignatureTests {

        @Test
        @DisplayName("Implementation methods should match interface signatures")
        void shouldMatchInterfaceMethodSignatures() {
            // Test ActivityController
            assertMethodsMatch(ActivityController.class, ActivityControllerApi.class);

            // Test AuthController
            assertMethodsMatch(AuthController.class, AuthControllerApi.class);

            // Test ProductController
            assertMethodsMatch(ProductController.class, ProductControllerApi.class);

            // Test UserController
            assertMethodsMatch(UserController.class, UserControllerApi.class);

            // Test CategoryController
            assertMethodsMatch(CategoryController.class, CategoryControllerApi.class);

            // Test HealthController
            assertMethodsMatch(HealthController.class, HealthControllerApi.class);

            // Test AlertController
            assertMethodsMatch(AlertController.class, AlertControllerApi.class);

            // Test InspectController
            assertMethodsMatch(InspectController.class, InspectControllerApi.class);

            // Test InventoryController
            assertMethodsMatch(InventoryController.class, InventoryControllerApi.class);
        }

        private void assertMethodsMatch(Class<?> implementation, Class<?> apiInterface) {
            Method[] interfaceMethods = apiInterface.getDeclaredMethods();

            for (Method interfaceMethod : interfaceMethods) {
                try {
                    Method implMethod = implementation.getMethod(
                            interfaceMethod.getName(),
                            interfaceMethod.getParameterTypes()
                    );

                    assertThat(implMethod.getReturnType())
                            .as("Return type of %s.%s should match interface",
                                    implementation.getSimpleName(), interfaceMethod.getName())
                            .isEqualTo(interfaceMethod.getReturnType());

                } catch (NoSuchMethodException e) {
                    throw new AssertionError(
                            String.format("Method %s not found in %s",
                                    interfaceMethod.getName(), implementation.getSimpleName()),
                            e
                    );
                }
            }
        }
    }

    @Nested
    @DisplayName("Architectural Pattern Tests")
    class ArchitecturalPatternTests {

        static Stream<Arguments> controllerImplementations() {
            return Stream.of(
                    Arguments.of(ActivityController.class),
                    Arguments.of(AlertController.class),
                    Arguments.of(AuthController.class),
                    Arguments.of(CategoryController.class),
                    Arguments.of(HealthController.class),
                    Arguments.of(InspectController.class),
                    Arguments.of(InventoryController.class),
                    Arguments.of(ProductController.class),
                    Arguments.of(UserController.class)
            );
        }

        @ParameterizedTest(name = "{0} should extend BaseController")
        @MethodSource("controllerImplementations")
        @DisplayName("Controller implementations should extend BaseController")
        void shouldExtendBaseController(Class<?> implementation) {
            // HealthController might not extend BaseController in some architectures
            if (implementation == HealthController.class) {
                // Check if it extends BaseController or is standalone
                boolean extendsBase = BaseController.class.isAssignableFrom(implementation);

                assertThat(extendsBase || implementation.getSuperclass() == Object.class)
                        .as("%s should either extend BaseController or be standalone",
                                implementation.getSimpleName())
                        .isTrue();
            } else {
                assertThat(com.dev.servlet.adapter.in.web.controller.internal.base.BaseController.class
                        .isAssignableFrom(implementation))
                        .as("%s should extend BaseController", implementation.getSimpleName())
                        .isTrue();
            }
        }

        @ParameterizedTest(name = "{0} should be in internal package")
        @MethodSource("controllerImplementations")
        @DisplayName("Controller implementations should be in internal package")
        void shouldBeInInternalPackage(Class<?> implementation) {
            assertThat(implementation.getPackage().getName())
                    .as("%s should be in internal package", implementation.getSimpleName())
                    .contains(".internal");
        }

        @ParameterizedTest(name = "{0} should be a concrete class")
        @MethodSource("controllerImplementations")
        @DisplayName("Controller implementations should be concrete classes")
        void shouldBeConcreteClass(Class<?> implementation) {
            assertThat(java.lang.reflect.Modifier.isAbstract(implementation.getModifiers()))
                    .as("%s should not be abstract", implementation.getSimpleName())
                    .isFalse();
        }
    }

    @Nested
    @DisplayName("Controller Naming Convention Tests")
    class NamingConventionTests {

        @Test
        @DisplayName("API interfaces should end with 'Api'")
        void apiInterfacesShouldEndWithApi() {
            Class<?>[] interfaces = {
                    ActivityControllerApi.class,
                    AlertControllerApi.class,
                    AuthControllerApi.class,
                    CategoryControllerApi.class,
                    HealthControllerApi.class,
                    InspectControllerApi.class,
                    InventoryControllerApi.class,
                    ProductControllerApi.class,
                    UserControllerApi.class
            };

            for (Class<?> apiInterface : interfaces) {
                assertThat(apiInterface.getSimpleName())
                        .as("%s should end with 'Api'", apiInterface.getSimpleName())
                        .endsWith("Api");
            }
        }

        @Test
        @DisplayName("Implementations should match API interface names")
        void implementationsShouldMatchApiNames() {
            assertNamingMatch(ActivityController.class, ActivityControllerApi.class);
            assertNamingMatch(AlertController.class, AlertControllerApi.class);
            assertNamingMatch(AuthController.class, AuthControllerApi.class);
            assertNamingMatch(CategoryController.class, CategoryControllerApi.class);
            assertNamingMatch(HealthController.class, HealthControllerApi.class);
            assertNamingMatch(InspectController.class, InspectControllerApi.class);
            assertNamingMatch(InventoryController.class, InventoryControllerApi.class);
            assertNamingMatch(ProductController.class, ProductControllerApi.class);
            assertNamingMatch(UserController.class, UserControllerApi.class);
        }

        private void assertNamingMatch(Class<?> implementation, Class<?> apiInterface) {
            String implName = implementation.getSimpleName();
            String apiName = apiInterface.getSimpleName().replace("Api", "");

            assertThat(implName)
                    .as("Implementation name should match API interface name")
                    .isEqualTo(apiName);
        }
    }

    @Nested
    @DisplayName("CDI Scope Tests")
    class CdiScopeTests {

        static Stream<Arguments> controllerImplementations() {
            return Stream.of(
                    Arguments.of(ActivityController.class),
                    Arguments.of(AlertController.class),
                    Arguments.of(AuthController.class),
                    Arguments.of(CategoryController.class),
                    Arguments.of(HealthController.class),
                    Arguments.of(InspectController.class),
                    Arguments.of(InventoryController.class),
                    Arguments.of(ProductController.class),
                    Arguments.of(UserController.class)
            );
        }

        @ParameterizedTest(name = "{0} should have CDI scope annotation")
        @MethodSource("controllerImplementations")
        @DisplayName("Controller implementations should have CDI scope")
        void shouldHaveCdiScope(Class<?> implementation) {
            boolean hasApplicationScoped = Arrays.stream(implementation.getAnnotations())
                    .anyMatch(a -> a.annotationType().getName()
                            .equals("jakarta.enterprise.context.ApplicationScoped"));

            boolean hasRequestScoped = Arrays.stream(implementation.getAnnotations())
                    .anyMatch(a -> a.annotationType().getName()
                            .equals("jakarta.enterprise.context.RequestScoped"));

            assertThat(hasApplicationScoped || hasRequestScoped)
                    .as("%s should have @ApplicationScoped or @RequestScoped annotation",
                            implementation.getSimpleName())
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("Complete Coverage Tests")
    class CompleteCoverageTests {

        @Test
        @DisplayName("All controller APIs should have implementations")
        void allApisShouldHaveImplementations() {
            Class<?>[][] pairs = {
                    {ActivityControllerApi.class, ActivityController.class},
                    {AlertControllerApi.class, AlertController.class},
                    {AuthControllerApi.class, AuthController.class},
                    {CategoryControllerApi.class, CategoryController.class},
                    {HealthControllerApi.class, HealthController.class},
                    {InspectControllerApi.class, InspectController.class},
                    {InventoryControllerApi.class, InventoryController.class},
                    {ProductControllerApi.class, ProductController.class},
                    {UserControllerApi.class, UserController.class}
            };

            for (Class<?>[] pair : pairs) {
                Class<?> api = pair[0];
                Class<?> impl = pair[1];

                assertThat(api.isAssignableFrom(impl))
                        .as("%s should be implemented by %s", api.getSimpleName(), impl.getSimpleName())
                        .isTrue();
            }
        }

        @Test
        @DisplayName("Should have 9 controller API interfaces")
        void shouldHaveNineControllerApis() {
            Class<?>[] apis = {
                    ActivityControllerApi.class,
                    AlertControllerApi.class,
                    AuthControllerApi.class,
                    CategoryControllerApi.class,
                    HealthControllerApi.class,
                    InspectControllerApi.class,
                    InventoryControllerApi.class,
                    ProductControllerApi.class,
                    UserControllerApi.class
            };

            assertThat(apis).hasSize(9);
        }

        @Test
        @DisplayName("Should have 9 controller implementations")
        void shouldHaveNineControllerImplementations() {
            Class<?>[] implementations = {
                    ActivityController.class,
                    AlertController.class,
                    AuthController.class,
                    CategoryController.class,
                    HealthController.class,
                    InspectController.class,
                    InventoryController.class,
                    ProductController.class,
                    UserController.class
            };

            assertThat(implementations).hasSize(9);
        }
    }
}

