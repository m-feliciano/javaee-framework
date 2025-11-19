package com.dev.servlet.controller.base;

import com.dev.servlet.controller.internal.ProductController;
import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.response.HttpResponse;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.core.response.IServletResponse;
import com.dev.servlet.core.util.EndpointParser;
import com.dev.servlet.core.util.JwtUtil;
import com.dev.servlet.core.util.PropertiesUtil;
import com.dev.servlet.domain.records.KeyPair;
import com.dev.servlet.domain.request.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class BaseRouterControllerTest {

    private BaseRouterController controller;
    private Request request;
    private EndpointParser endpoint;
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        controller = new ProductController();
        request = mock(Request.class);
        endpoint = mock(EndpointParser.class);
        jwtUtil = mock(JwtUtil.class);

        when(endpoint.path()).thenReturn("list");
        when(endpoint.controller()).thenReturn("Test");
        when(endpoint.apiVersion()).thenReturn("v1");

        when(request.getMethod()).thenReturn("GET");
        when(request.getToken()).thenReturn("valid-bearerToken");
    }

    private static IServletResponse getServletResponse() {
        return new IServletResponse() {
            @Override
            public int statusCode() {
                return 200;
            }

            @Override
            public Set<KeyPair> body() {
                return Set.of(new KeyPair("response", "Test response"));
            }

            @Override
            public String next() {
                return "";
            }
        };
    }

    @Test
    void route_WithValidEndpoint_ShouldCallCorrectMethod() throws Exception {
        when(request.getPayload(any())).thenReturn(new Object());

        try (MockedStatic<PropertiesUtil> propertiesUtil = mockStatic(PropertiesUtil.class)) {
            propertiesUtil.when(() -> PropertiesUtil.getProperty(eq("security.jwt.key")))
                    .thenReturn("test-key-832dcrf5t6yhbjijim0987y");
        }

        ProductController controllerSpy = spy((ProductController) controller);
        doReturn(getServletResponse())
                .when(controllerSpy)
                .list(any(), any());

        IHttpResponse<Set<KeyPair>> response = controllerSpy.route(endpoint, request);

        assertNotNull(response);
        assertEquals(200, response.statusCode());
        assertEquals("Test response", response.body().iterator().next().getValue());
    }

    @Test
    void route_WithValidEndpointAndPropertyParam_ShouldPassPropertyValue() throws Exception {
        when(endpoint.path()).thenReturn("scrape");

        try (MockedStatic<PropertiesUtil> propertiesUtil = mockStatic(PropertiesUtil.class)) {
            propertiesUtil.when(() -> PropertiesUtil.getProperty(eq("test.property"), anyString()))
                    .thenReturn("property-value");

            propertiesUtil.when(() -> PropertiesUtil.getProperty(eq("env"), anyString()))
                    .thenReturn("development");

            propertiesUtil.when(() -> PropertiesUtil.getProperty(eq("security.jwt.key")))
                    .thenReturn("test-key-832dcrf5t6yhbjijim0987y");

            ProductController controllerSpy = spy((ProductController) controller);
            HttpResponse<Object> httpResponse = HttpResponse.next("").build();
            doReturn(httpResponse)
                    .when(controllerSpy)
                    .scrape(any(), any(), any());

            IHttpResponse<Void> response = controllerSpy.route(endpoint, request);

            assertNotNull(response);
            assertEquals(200, response.statusCode());
        }
    }

    @Test
    void route_WithInvalidEndpoint_ShouldThrowException() {
        when(endpoint.path()).thenReturn("nonexistent");

        ServiceException exception = assertThrows(ServiceException.class, () -> controller.route(endpoint, request));
        assertEquals(500, exception.getCode());
        assertEquals("Endpoint not implemented: /nonexistent", exception.getMessage());
    }

    @Test
    void route_WithValidEndpointButValidationFails_ShouldThrowException() {
        when(request.getMethod()).thenReturn("POST");

        ServiceException exception = assertThrows(ServiceException.class, () -> controller.route(endpoint, request));
        assertEquals(405, exception.getCode());
        assertEquals("Method not allowed.", exception.getMessage());
    }
}
