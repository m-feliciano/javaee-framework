package com.dev.servlet.adapter;

import com.dev.servlet.adapter.internal.HttpExecutor;
import com.dev.servlet.controller.base.BaseRouterController;
import com.dev.servlet.core.response.HttpResponse;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.core.util.BeanUtil;
import com.dev.servlet.core.util.EndpointParser;
import com.dev.servlet.core.util.HttpExecutorTestLogSuppressor;
import com.dev.servlet.domain.transfer.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(HttpExecutorTestLogSuppressor.class)
class HttpExecutorTest {

    private IHttpExecutor<?> httpExecutor;
    private Request request;
    private EndpointParser parser;

    @BeforeEach
    void setUp() {
        httpExecutor = new HttpExecutor<>();
        request = mock(Request.class);
        parser = mock(EndpointParser.class);

        when(parser.apiVersion()).thenReturn("v1");
        when(parser.controller()).thenReturn("list");
        when(parser.path()).thenReturn("/test");

        when(request.getEndpoint()).thenReturn("/api/v1/testService/test");
    }

    @Test
    @DisplayName(
            "Test send method with a valid request and a successful response. " +
            "It should return the expected IHttpResponse object.")
    void testCall_Success() {
        IHttpResponse<Object> expectedResponse = HttpResponse.next("Next").build();

        // Mock the static method to return the mocked parser
        try (MockedStatic<EndpointParser> parserMock = mockStatic(EndpointParser.class);
             MockedStatic<BeanUtil> beanUtilMock = mockStatic(BeanUtil.class)) {

            parserMock.when(() -> EndpointParser.of(anyString()))
                    .thenReturn(parser);


            beanUtilMock.when(BeanUtil::getResolver)
                    .thenReturn(mock(BeanUtil.DependencyResolver.class));

            beanUtilMock.when(() -> BeanUtil.getResolver().getBean(anyString()))
                    .thenReturn(new BaseRouterController() {
                        @Override
                        public IHttpResponse<?> route(EndpointParser endpoint, Request request) {
                            return expectedResponse;
                        }
                    });

            IHttpResponse<?> response = httpExecutor.call(request);

            assertNotNull(response);
            assertEquals(expectedResponse, response);
            assertEquals("Next", response.next());
            assertNull(response.error());
        }
    }

    @Test
    @DisplayName(
            "Test send method with a valid request and an error response. " +
            "It should return the expected IHttpResponse object with errors.")
    void testCall_ServiceException() {
        try (MockedStatic<EndpointParser> parserMock = mockStatic(EndpointParser.class);
             MockedStatic<BeanUtil> beanUtilMock = mockStatic(BeanUtil.class)) {

            BeanUtil.DependencyResolver resolver = mock(BeanUtil.DependencyResolver.class);
            beanUtilMock.when(BeanUtil::getResolver).thenReturn(resolver);
            parserMock.when(() -> EndpointParser.of(anyString())).thenReturn(parser);

            IHttpResponse<?> response = httpExecutor.call(request);

            assertNotNull(response);
            assertEquals(400, response.statusCode());
            assertEquals("Error resolving service endpoint: /test", response.error());
        }
    }

    @Test
    @DisplayName(
            "Test send method with a valid request and an unexpected exception. " +
            "It should return an IHttpResponse object with a 500 status code.")
    void testCall_UnexpectedException() {
        try (MockedStatic<EndpointParser> parserMock = mockStatic(EndpointParser.class)) {
            parserMock.when(() -> EndpointParser.of(anyString())).thenThrow(new RuntimeException("Unexpected error"));

            IHttpResponse<?> response = httpExecutor.call(request);

            assertNotNull(response);
            assertEquals(400, response.statusCode());
            assertEquals("Invalid endpoint: /api/v1/testService/test", response.error());
        }
    }
}

