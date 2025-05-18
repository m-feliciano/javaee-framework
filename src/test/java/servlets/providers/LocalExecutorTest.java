package servlets.providers;

import com.dev.servlet.controllers.router.BaseRouterController;
import com.dev.servlet.interfaces.IHttpResponse;
import com.dev.servlet.pojo.records.HttpResponse;
import com.dev.servlet.pojo.records.Request;
import com.dev.servlet.providers.LocalExecutor;
import com.dev.servlet.utils.BeanUtil;
import com.dev.servlet.utils.EndpointParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class LocalExecutorTest {

    private LocalExecutor<?> localExecutor;
    private Request request;
    private EndpointParser parser;

    @BeforeEach
    void setUp() {
        localExecutor = LocalExecutor.newInstance();
        request = mock(Request.class);
        parser = mock(EndpointParser.class);

        when(parser.getApiVersion()).thenReturn("v1");
        when(parser.getService()).thenReturn("/testService");
        when(parser.getServiceName()).thenReturn("/test");
        when(request.endpoint()).thenReturn("/api/v1/testService/test");
    }

    @Test
    @DisplayName(
            "Test send method with a valid request and a successful response. " +
            "It should return the expected IHttpResponse object.")
    void testSend_Success() throws Exception {
        BaseRouterController controller = mock(BaseRouterController.class);
        IHttpResponse<Object> expectedResponse = HttpResponse.ofNext("Next");

        when(request.endpoint()).thenReturn("/test");

        try (MockedStatic<EndpointParser> parserMock = mockStatic(EndpointParser.class);
             MockedStatic<BeanUtil> beanUtilMock = mockStatic(BeanUtil.class)) {

            // Mock the static method to return the mocked parser
            parserMock.when(() -> EndpointParser.of(anyString())).thenReturn(parser);

            when(BeanUtil.getResolver()).thenReturn(mock(BeanUtil.DependencyResolver.class));
            beanUtilMock.when(() -> BeanUtil.getResolver().getService("/testService")).thenReturn(controller);

            when(controller.route(parser, request)).thenReturn(expectedResponse);

            IHttpResponse<?> response = localExecutor.send(request);

            assertNotNull(response);
            assertEquals(expectedResponse, response);
            assertEquals("Next", response.next());
            Assertions.assertNull(response.errors());
        }
    }

    @Test
    @DisplayName(
            "Test send method with a valid request and an error response. " +
            "It should return the expected IHttpResponse object with errors.")
    void testSend_ServiceException() {
        try (MockedStatic<EndpointParser> parserMock = mockStatic(EndpointParser.class);
             MockedStatic<BeanUtil> beanUtilMock = mockStatic(BeanUtil.class)) {

            parserMock.when(() -> EndpointParser.of(anyString())).thenReturn(parser);
            beanUtilMock.when(() -> BeanUtil.getResolver().getService("testService")).thenReturn(null);

            IHttpResponse<?> response = localExecutor.send(request);

            assertNotNull(response);
            assertEquals(400, response.statusCode());
            assertEquals("Error resolving service method for path: /testService", response.errors().iterator().next());
        }
    }

    @Test
    @DisplayName(
            "Test send method with a valid request and an unexpected exception. " +
            "It should return an IHttpResponse object with a 500 status code.")
    void testSend_UnexpectedException() {
        when(request.endpoint()).thenReturn("/test");
        try (MockedStatic<EndpointParser> parserMock = mockStatic(EndpointParser.class)) {

            parserMock.when(() -> EndpointParser.of("/test")).thenThrow(new RuntimeException("Unexpected error"));

            IHttpResponse<?> response = localExecutor.send(request);

            assertNotNull(response);
            assertEquals(500, response.statusCode());
            assertEquals("An unexpected error occurred.", response.errors().iterator().next());
        }
    }
}