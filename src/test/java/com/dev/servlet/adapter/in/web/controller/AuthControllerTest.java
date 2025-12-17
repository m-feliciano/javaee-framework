package com.dev.servlet.adapter.in.web.controller;

import com.dev.servlet.adapter.in.web.controller.internal.AuthController;
import com.dev.servlet.adapter.in.web.dto.HttpResponse;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.application.port.in.auth.FormPort;
import com.dev.servlet.application.port.in.auth.HomePagePort;
import com.dev.servlet.application.port.in.auth.LoginPort;
import com.dev.servlet.application.port.in.auth.LogoutPort;
import com.dev.servlet.application.port.in.auth.RegisterPagePort;
import com.dev.servlet.application.transfer.request.LoginRequest;
import com.dev.servlet.application.transfer.response.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test suite for AuthController implementation via AuthControllerApi interface.
 * Tests authentication flows including login, logout, form rendering, and registration page forwarding.
 */
@DisplayName("AuthController Tests")
class AuthControllerTest extends BaseControllerTest {

    private static final String HOMEPAGE = "product/list";
    private static final String REGISTER_PAGE = "forward:pages/auth/register.jsp";
    private static final String LOGIN_FORM = "forward:pages/auth/login.jsp";
    @Mock
    private LoginPort loginPort;
    @Mock
    private FormPort formPort;
    @Mock
    private LogoutPort logoutPort;
    @Mock
    private HomePagePort homePagePort;
    @Mock
    private RegisterPagePort registerPagePort;
    @InjectMocks
    private AuthController authController;

    @Override
    protected void setupAdditionalMocks() {
        authController.setJwtUtils(authenticationPort);
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should successfully login with valid credentials")
        void shouldLoginSuccessfully() throws Exception {
            // Arrange
            LoginRequest request = new LoginRequest("testuser", "password123");

            UserResponse userResponse = UserResponse.builder()
                    .id(USER_ID)
                    .login("testuser")
                    .build();

            IHttpResponse<UserResponse> expectedResponse = HttpResponse.ok(userResponse)
                    .next("redirect:/" + HOMEPAGE)
                    .build();

            when(loginPort.login(any(LoginRequest.class), anyString())).thenReturn(expectedResponse);

            // Act
            IHttpResponse<UserResponse> response = authController.login(request, HOMEPAGE);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isNotNull();
            assertThat(response.body().getId()).isEqualTo(USER_ID);
            assertThat(response.body().getLogin()).isEqualTo("testuser");
            assertThat(response.next()).contains("redirect:");
            assertThat(response.next()).contains(HOMEPAGE);

            verify(loginPort).login(eq(request), eq("redirect:/" + HOMEPAGE));
        }

        @Test
        @DisplayName("Should handle login with custom homepage")
        void shouldHandleLoginWithCustomHomepage() throws Exception {
            // Arrange
            LoginRequest request = new LoginRequest("user", "pass");
            String customHomepage = "dashboard";

            when(loginPort.login(any(LoginRequest.class), anyString())).thenReturn(
                    HttpResponse.ok(UserResponse.builder().id(USER_ID).build())
                            .next("redirect:/" + customHomepage)
                            .build()
            );

            // Act
            IHttpResponse<UserResponse> response = authController.login(request, customHomepage);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.next()).isEqualTo("redirect:/" + customHomepage);
            verify(loginPort).login(eq(request), eq("redirect:/" + customHomepage));
        }
    }

    @Nested
    @DisplayName("Logout Tests")
    class LogoutTests {

        @Test
        @DisplayName("Should successfully logout user")
        void shouldLogoutSuccessfully() {
            // Arrange
            when(homePagePort.homePage()).thenReturn("forward:pages/home.jsp");

            // Act
            IHttpResponse<String> response = authController.logout(VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isNull();
            assertThat(response.next()).isEqualTo("forward:pages/home.jsp");

            verify(logoutPort).logout(VALID_AUTH_TOKEN);
            verify(homePagePort).homePage();
        }

        @Test
        @DisplayName("Should redirect to homepage after logout")
        void shouldRedirectToHomepageAfterLogout() {
            // Arrange
            String homePage = "forward:pages/auth/login.jsp";
            when(homePagePort.homePage()).thenReturn(homePage);

            // Act
            IHttpResponse<String> response = authController.logout(VALID_AUTH_TOKEN);

            // Assert
            assertThat(response.next()).isEqualTo(homePage);
        }
    }

    @Nested
    @DisplayName("Form Rendering Tests")
    class FormTests {

        @Test
        @DisplayName("Should render login form for authenticated user")
        void shouldRenderFormForAuthenticatedUser() {
            // Arrange
            when(formPort.form(VALID_AUTH_TOKEN, HOMEPAGE)).thenReturn(LOGIN_FORM);

            // Act
            IHttpResponse<String> response = authController.form(VALID_AUTH_TOKEN, HOMEPAGE);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isNull();
            assertThat(response.next()).isEqualTo(LOGIN_FORM);

            verify(formPort).form(VALID_AUTH_TOKEN, HOMEPAGE);
        }

        @Test
        @DisplayName("Should render form with correct homepage context")
        void shouldRenderFormWithHomepageContext() {
            // Arrange
            String customHomepage = "user/profile";
            when(formPort.form(anyString(), eq(customHomepage))).thenReturn(LOGIN_FORM);

            // Act
            IHttpResponse<String> response = authController.form(VALID_AUTH_TOKEN, customHomepage);

            // Assert
            assertThat(response).isNotNull();
            verify(formPort).form(VALID_AUTH_TOKEN, customHomepage);
        }
    }

    @Nested
    @DisplayName("Register Page Tests")
    class RegisterPageTests {

        @Test
        @DisplayName("Should forward to register page")
        void shouldForwardToRegisterPage() {
            // Arrange
            when(registerPagePort.registerPage()).thenReturn(REGISTER_PAGE);

            // Act
            IHttpResponse<String> response = authController.forwardRegister();

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isNull();
            assertThat(response.next()).isEqualTo(REGISTER_PAGE);

            verify(registerPagePort).registerPage();
        }

        @Test
        @DisplayName("Should not require authentication for register page")
        void shouldNotRequireAuthForRegisterPage() {
            // Arrange
            when(registerPagePort.registerPage()).thenReturn(REGISTER_PAGE);

            // Act - No auth token provided
            IHttpResponse<String> response = authController.forwardRegister();

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.next()).isEqualTo(REGISTER_PAGE);
        }
    }

    @Nested
    @DisplayName("Controller Implementation Tests")
    class ImplementationTests {

        @Test
        @DisplayName("Should implement AuthControllerApi interface")
        void shouldImplementInterface() {
            assertThat(authController).isInstanceOf(AuthControllerApi.class);
        }

        @Test
        @DisplayName("Should have all required port dependencies injected")
        void shouldHaveAllDependenciesInjected() {
            assertThat(authController).extracting("loginPort").isNotNull();
            assertThat(authController).extracting("formPort").isNotNull();
            assertThat(authController).extracting("logoutPort").isNotNull();
            assertThat(authController).extracting("homePagePort").isNotNull();
            assertThat(authController).extracting("registerPagePort").isNotNull();
        }
    }
}

