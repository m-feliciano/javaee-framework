package com.servletstack.adapter.in.web.frontcontroller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ErrorResponseWriter Tests")
class ErrorResponseWriterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private ErrorResponseWriter errorResponseWriter;

    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() {
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);

        lenient().when(request.getContextPath()).thenReturn("/app");
    }

    @Nested
    @DisplayName("Error Response Tests")
    class ErrorResponseTests {

        @Test
        @DisplayName("Should write 404 error response with HTML")
        void shouldWrite404ErrorResponse() throws Exception {
            // Arrange
            when(response.getWriter()).thenReturn(printWriter);

            // Act
            errorResponseWriter.write(request, response, 404, "Resource not found");

            // Assert
            verify(response).setStatus(404);
            verify(response).setContentType("text/html");
            verify(response).setCharacterEncoding("UTF-8");

            String output = stringWriter.toString();
            assertThat(output).contains("404");
            assertThat(output).contains("Resource not found");
            assertThat(output).contains("cat_error404.gif");
        }

        @Test
        @DisplayName("Should write 500 error response")
        void shouldWrite500ErrorResponse() throws Exception {
            // Arrange
            when(response.getWriter()).thenReturn(printWriter);

            // Act
            errorResponseWriter.write(request, response, 500, "Internal server error");

            // Assert
            verify(response).setStatus(500);
            verify(response).setContentType("text/html");

            String output = stringWriter.toString();
            assertThat(output).contains("500");
            assertThat(output).contains("Internal server error");
        }

        @Test
        @DisplayName("Should write 400 error response")
        void shouldWrite400ErrorResponse() throws Exception {
            // Arrange
            when(response.getWriter()).thenReturn(printWriter);

            // Act
            errorResponseWriter.write(request, response, 400, "Bad request");

            // Assert
            verify(response).setStatus(400);

            String output = stringWriter.toString();
            assertThat(output).contains("400");
            assertThat(output).contains("Bad request");
        }

        @Test
        @DisplayName("Should write 403 error response")
        void shouldWrite403ErrorResponse() throws Exception {
            // Arrange
            when(response.getWriter()).thenReturn(printWriter);

            // Act
            errorResponseWriter.write(request, response, 403, "Forbidden");

            // Assert
            verify(response).setStatus(403);

            String output = stringWriter.toString();
            assertThat(output).contains("403");
            assertThat(output).contains("Forbidden");
        }

        @Test
        @DisplayName("Should write 401 error response")
        void shouldWrite401ErrorResponse() throws Exception {
            // Arrange
            when(response.getWriter()).thenReturn(printWriter);

            // Act
            errorResponseWriter.write(request, response, 401, "Unauthorized");

            // Assert
            verify(response).setStatus(401);

            String output = stringWriter.toString();
            assertThat(output).contains("401");
            assertThat(output).contains("Unauthorized");
        }
    }

    @Nested
    @DisplayName("HTML Template Tests")
    class HtmlTemplateTests {

        @Test
        @DisplayName("Should include error image in HTML")
        void shouldIncludeErrorImage() throws Exception {
            // Arrange
            when(response.getWriter()).thenReturn(printWriter);
            when(request.getContextPath()).thenReturn("/myapp");

            // Act
            errorResponseWriter.write(request, response, 404, "Not found");

            // Assert
            String output = stringWriter.toString();
            assertThat(output).contains("/myapp/resources/assets/images/cat_error404.gif");
        }

        @Test
        @DisplayName("Should include error status message")
        void shouldIncludeStatusMessage() throws Exception {
            // Arrange
            when(response.getWriter()).thenReturn(printWriter);

            // Act
            errorResponseWriter.write(request, response, 404, "Page not found");

            // Assert
            String output = stringWriter.toString();
            assertThat(output).contains("Page not found");
        }

        @Test
        @DisplayName("Should generate valid HTML structure")
        void shouldGenerateValidHtml() throws Exception {
            // Arrange
            when(response.getWriter()).thenReturn(printWriter);

            // Act
            errorResponseWriter.write(request, response, 500, "Server error");

            // Assert
            String output = stringWriter.toString();
            assertThat(output).contains("<html");
            assertThat(output).contains("</html>");
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle exception when writing response")
        void shouldHandleExceptionWhenWriting() throws Exception {
            // Arrange
            when(response.getWriter()).thenThrow(new RuntimeException("Writer error"));

            // Act & Assert - Should not throw exception
            errorResponseWriter.write(request, response, 500, "Error");

            // Verify that status and content type were set before exception
            verify(response).setStatus(500);
            verify(response).setContentType("text/html");
        }
    }

    @Nested
    @DisplayName("Context Path Handling Tests")
    class ContextPathTests {

        @Test
        @DisplayName("Should handle empty context path")
        void shouldHandleEmptyContextPath() throws Exception {
            // Arrange
            when(request.getContextPath()).thenReturn("");
            when(response.getWriter()).thenReturn(printWriter);

            // Act
            errorResponseWriter.write(request, response, 404, "Not found");

            // Assert
            String output = stringWriter.toString();
            assertThat(output).contains("/resources/assets/images/cat_error404.gif");
        }

        @Test
        @DisplayName("Should handle context path with leading slash")
        void shouldHandleContextPathWithSlash() throws Exception {
            // Arrange
            when(request.getContextPath()).thenReturn("/webapp");
            when(response.getWriter()).thenReturn(printWriter);

            // Act
            errorResponseWriter.write(request, response, 404, "Not found");

            // Assert
            String output = stringWriter.toString();
            assertThat(output).contains("/webapp/resources/assets/images/");
        }
    }
}

