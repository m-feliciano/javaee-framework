package com.dev.servlet.web.dispatcher.impl;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.port.in.auth.AuthCookieUseCasePort;
import com.dev.servlet.application.port.out.AuthenticationPort;
import com.dev.servlet.application.transfer.response.UserResponse;
import com.dev.servlet.application.usecase.user.UserDetailsUseCase;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.RequestMethod;
import com.dev.servlet.shared.util.URIUtils;
import com.dev.servlet.web.Request;
import com.dev.servlet.web.builder.HtmlTemplate;
import com.dev.servlet.web.builder.RequestBuilder;
import com.dev.servlet.web.dispatcher.HttpExecutor;
import com.dev.servlet.web.dispatcher.IServletDispatcher;
import com.dev.servlet.web.response.IHttpResponse;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.io.PrintWriter;
import java.text.MessageFormat;

import static com.dev.servlet.shared.enums.ConstantUtils.BEARER_PREFIX;

@Setter
@Slf4j
@NoArgsConstructor
@RequestScoped
public class ServletDispatcherImpl implements IServletDispatcher {
    public static final String LOGOUT = "logout";

    @Inject
    private AuthCookieUseCasePort authCookieUseCasePort;
    @Inject
    private UserDetailsUseCase userDetailsUseCase;
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private HttpExecutor<?> httpExecutor;

    private static Request requestOf(HttpServletRequest httpServletRequest) {
        return RequestBuilder.newBuilder().servletRequest(httpServletRequest).complete().retry(1).build();
    }

    @Interceptors({LogExecutionTimeInterceptor.class})
    public void dispatch(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        this.execute(servletRequest, servletResponse);
    }

    private void execute(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        String requestURI = servletRequest.getRequestURI();
        String method = servletRequest.getMethod();
        log.info("Processing request: {} {}", method, requestURI);
        try {
            Request request = requestOf(servletRequest);
            IHttpResponse<?> httpResponse = httpExecutor.send(request);
            processResponse(servletRequest, servletResponse, request, httpResponse);
        } catch (ApplicationException e) {
            log.error("Service exception for {} {}", method, requestURI, e);
            writeResponseError(servletRequest, servletResponse, e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected exception for {} {}", method, requestURI, e);
            String message = "An unexpected error occurred. Please try again later.";
            writeResponseError(servletRequest, servletResponse, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
        }
    }

    private void setRequestAttributes(HttpServletRequest httpRequest, IHttpResponse<?> response) {
        httpRequest.setAttribute("response", response);
        for (var key : httpRequest.getParameterMap().keySet()) {
            httpRequest.setAttribute(key, httpRequest.getParameter(key));
        }
    }

    private void handleResponseErrors(HttpServletRequest httpRequest, HttpServletResponse httpResponse, IHttpResponse<?> response) {
        if (response.error() != null && response.reasonText() == null) {
            String errors = String.join(", ", response.error());
            writeResponseError(httpRequest, httpResponse, response.statusCode(), errors);
        } else {
            httpResponse.setStatus(response.statusCode());
        }
    }

    private void processResponseData(HttpServletRequest httpRequest,
                                     HttpServletResponse httpResponse,
                                     Request request,
                                     IHttpResponse<?> response) {
        log.debug("Response status: {}", response.statusCode());
        if (RequestMethod.POST.isEquals(request.getMethod()) && response.body() instanceof UserResponse user) {
            if (user.hasToken()) {
                authCookieUseCasePort.setAuthCookies(httpResponse, user.getToken(), user.getRefreshToken());
            }
        }
        httpResponse.setHeader("X-Correlation-ID", MDC.get("correlationId"));
        addUserToRequest(httpRequest);
        setRequestAttributes(httpRequest, response);
        handleResponseErrors(httpRequest, httpResponse, response);
        if (request.contains(LOGOUT)) {
            authCookieUseCasePort.clearCookies(httpResponse);
        }
    }

    private void addUserToRequest(HttpServletRequest httpRequest) {
        try {
            String token = authCookieUseCasePort.getTokenFromCookie(httpRequest, authCookieUseCasePort.getAccessTokenCookieName());
            if (StringUtils.isNotBlank(token)) {
                User user = authenticationPort.extractUser(BEARER_PREFIX + token);
                UserResponse response = userDetailsUseCase.get(user.getId(), token);
                httpRequest.setAttribute("user", response);
            }
        } catch (Exception e) {
            log.warn("Unable to add user to request", e);
        }
    }

    private void processResponse(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Request request, IHttpResponse<?> response) throws ApplicationException {
        processResponseData(httpRequest, httpResponse, request, response);
        if (response.json()) {
            sendJsonResponse(httpResponse, response);
            return;
        }
        if (response.next() == null) return;
        String[] path = response.next().split(":");
        if (path.length != 2) {
            throw new ApplicationException("Cannot parse URL: " + response.next());
        }
        String pathAction = path[0];
        String pathUrl = path[1];
        try {
            if ("forward".equalsIgnoreCase(pathAction)) {
                httpRequest.getRequestDispatcher("/WEB-INF/view/" + pathUrl).forward(httpRequest, httpResponse);
            } else {
                httpResponse.sendRedirect(pathUrl);
            }
        } catch (Exception e) {
            String message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            throw new ApplicationException("Error processing httpRequest: " + message);
        }
    }

    private void sendJsonResponse(HttpServletResponse httpResponse, IHttpResponse<?> response) throws ApplicationException {
        httpResponse.setContentType("application/json");
        httpResponse.setCharacterEncoding("UTF-8");
        try (PrintWriter writer = httpResponse.getWriter()) {
            writer.write(String.valueOf(response.body()));
            writer.flush();
        } catch (Exception e) {
            String message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            throw new ApplicationException("Error writing JSON response: " + message);
        }
    }

    private void writeResponseError(HttpServletRequest httpRequest, HttpServletResponse httpResponse, int status, String message) {
        String statusMessage = URIUtils.getErrorMessage(status);
        String funnyGif = "cat_error404.gif";
        String image = MessageFormat.format("{0}/resources/assets/images/{1}", httpRequest.getContextPath(), funnyGif);
        String htmlErrorPage = HtmlTemplate.newBuilder()
                .error(status)
                .subTitle(statusMessage)
                .message(message)
                .image(image)
                .build();
        httpResponse.setStatus(status);
        httpResponse.setContentType("text/html");
        httpResponse.setCharacterEncoding("UTF-8");
        try (PrintWriter writer = httpResponse.getWriter()) {
            writer.write(htmlErrorPage);
            writer.flush();
        } catch (Exception e) {
            String cause = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            log.error("Error writing response: {}", cause);
        }
    }
}
