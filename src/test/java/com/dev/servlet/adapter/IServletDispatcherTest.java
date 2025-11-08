package com.dev.servlet.adapter;


import com.dev.servlet.adapter.internal.HttpExecutor;
import com.dev.servlet.adapter.internal.ServletDispatcherImpl;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.core.util.URIUtils;
import com.dev.servlet.domain.transfer.Request;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("ALL")
class IServletDispatcherTest {

    private IServletDispatcher servletDispatcher;
    private HttpServletRequest httpRequest;
    private HttpServletResponse httpResponse;
    private IHttpExecutor httpExecutor;
    private IHttpResponse httpResponseMock;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws IOException {
        servletDispatcher = new ServletDispatcherImpl();
        httpRequest = mock(HttpServletRequest.class);
        httpResponse = mock(HttpServletResponse.class);
        httpExecutor = mock(HttpExecutor.class);
        httpResponseMock = mock(IHttpResponse.class);
        printWriter = mock(PrintWriter.class);

        var servletDispatcher = (ServletDispatcherImpl) this.servletDispatcher;
        servletDispatcher.setHttpExecutor(httpExecutor);

        when(httpResponse.getWriter()).thenReturn(printWriter);
        when(httpRequest.getSession()).thenReturn(mock(HttpSession.class));
    }

    @Test
    @DisplayName(
            "Test dispatch method with a forward response should forward the request to the specified URL.")
    void testDispatch_Success() throws Exception {
        when(httpResponseMock.next()).thenReturn("forward:success.jsp");

        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        when(httpRequest.getRequestDispatcher("/WEB-INF/view/success.jsp")).thenReturn(dispatcher);

        try (MockedStatic<HttpExecutor> executorMockStatic = mockStatic(HttpExecutor.class);
             MockedStatic<URIUtils> uriUtilsMockedStatic = mockStatic(URIUtils.class)) {

            when(httpExecutor.call(any(Request.class))).thenReturn(httpResponseMock);
            servletDispatcher.dispatch(httpRequest, httpResponse);
            verify(dispatcher).forward(httpRequest, httpResponse);
            verify(httpExecutor).call(any(Request.class));
        }
    }

    @Test
    @DisplayName(
            "Test dispatch method with a redirect response should send a redirect to the specified URL.")
    void testDispatch_SendRedirect() throws Exception {
        when(httpResponseMock.next()).thenReturn("redirect:/somewhere");

        try (MockedStatic<HttpExecutor> executorMockStatic = mockStatic(HttpExecutor.class);
             MockedStatic<URIUtils> uriUtilsMockedStatic = mockStatic(URIUtils.class)) {

            when(httpExecutor.call(any(Request.class))).thenReturn(httpResponseMock);
            servletDispatcher.dispatch(httpRequest, httpResponse);
            verify(httpResponse).sendRedirect("/somewhere");
        }
    }
}