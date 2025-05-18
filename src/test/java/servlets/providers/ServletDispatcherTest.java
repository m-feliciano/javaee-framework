package servlets.providers;

import com.dev.servlet.builders.RequestBuilder;
import com.dev.servlet.interfaces.IHttpExecutor;import com.dev.servlet.interfaces.IHttpResponse;
import com.dev.servlet.interfaces.IRateLimiter;
import com.dev.servlet.pojo.records.Request;
import com.dev.servlet.providers.LocalExecutor;
import com.dev.servlet.providers.ServletDispatcher;
import com.dev.servlet.utils.URIUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("ALL")
class ServletDispatcherTest {

    private ServletDispatcher servletDispatcher;
    private HttpServletRequest httpRequest;
    private HttpServletResponse httpResponse;
    private IRateLimiter rateLimiter;
    private IHttpExecutor httpExecutor;
    private IHttpResponse httpResponseMock;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws IOException {
        servletDispatcher = new ServletDispatcher();
        httpRequest = mock(HttpServletRequest.class);
        httpResponse = mock(HttpServletResponse.class);
        rateLimiter = mock(IRateLimiter.class);
        httpExecutor = mock(LocalExecutor.class);
        httpResponseMock = mock(IHttpResponse.class);
        printWriter = mock(PrintWriter.class);

        servletDispatcher.setRateLimiter(rateLimiter);
        servletDispatcher.setRateLimitEnabled(true);

        when(httpResponse.getWriter()).thenReturn(printWriter);
        when(httpRequest.getSession()).thenReturn(mock(HttpSession.class));
        when(rateLimiter.acquireOrWait(anyInt())).thenReturn(true);
    }

    @Test
    @DisplayName(
            "Test dispatch method with a forward response and a valid rate limit. " +
            "It should forward the request to the specified URL.")
    void testDispatch_Success() throws Exception {
        // Arrange: Set up mocks and expected behavior
        when(httpResponseMock.next()).thenReturn("forward:success.jsp");

        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        when(httpRequest.getRequestDispatcher("/WEB-INF/view/success.jsp")).thenReturn(dispatcher);

        try (MockedStatic<LocalExecutor> executorMockStatic = mockStatic(LocalExecutor.class);
             MockedStatic<URIUtils> uriUtilsMockedStatic = mockStatic(URIUtils.class)) {

            when(httpExecutor.send(any(Request.class))).thenReturn(httpResponseMock);
            executorMockStatic.when(LocalExecutor::newInstance).thenReturn(httpExecutor);

            // Act: Call the method under test
            servletDispatcher.dispatch(httpRequest, httpResponse);

            // Assert: Verify the expected behavior
            verify(dispatcher).forward(httpRequest, httpResponse);
            verify(rateLimiter).acquireOrWait(ServletDispatcher.WAIT_TIME);
            verify(httpExecutor).send(any(Request.class));
        }
    }

    @Test
    @DisplayName(
            "Test dispatch method with a redirect response and a valid rate limit. " +
            "It should send a redirect to the specified URL.")
    void testDispatch_SendRedirect() throws Exception {
        // Arrange: Set up mocks and expected behavior
        when(httpResponseMock.next()).thenReturn("redirect:/somewhere");

        try (MockedStatic<LocalExecutor> executorMockStatic = mockStatic(LocalExecutor.class);
             MockedStatic<URIUtils> uriUtilsMockedStatic = mockStatic(URIUtils.class)) {

            when(httpExecutor.send(any(Request.class))).thenReturn(httpResponseMock);
            executorMockStatic.when(LocalExecutor::newInstance).thenReturn(httpExecutor);

            // Act: Call the method under test
            servletDispatcher.dispatch(httpRequest, httpResponse);

            // Assert: Verify the expected behavior
            verify(httpResponse).sendRedirect("/somewhere");
        }
    }

    @Test
    @DisplayName(
            "Test dispatch method with a forward response and an invalid rate limit. " +
            "It should set the status to SC_SERVICE_UNAVAILABLE.")
    void testDispatch_RateLimitExceeded() {
        when(rateLimiter.acquireOrWait(anyInt())).thenReturn(false);

        servletDispatcher.dispatch(httpRequest, httpResponse);

        verify(httpResponse).setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        verify(printWriter).write(contains("Please try again later."));
    }

    @Test
    @DisplayName(
            "Test dispatch method with a forward response and a valid rate limit. " +
            "It should set the status to SC_OK.")
    void testDispatch_UnexpectedException() {
        when(rateLimiter.acquireOrWait(anyInt())).thenReturn(true);

        try (MockedStatic<Request> requestMock = mockStatic(Request.class)) {
            requestMock.when(() -> RequestBuilder.newBuilder().httpServletRequest(httpRequest).complete().retry(1).build())
                    .thenThrow(new RuntimeException("Unexpected error"));


            servletDispatcher.dispatch(httpRequest, httpResponse);

            verify(httpResponse).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            verify(printWriter).write(contains("An error occurred while processing the request."));
        }
    }
}
