package com.dev.servlet.adapter.in.web.dispatcher.impl;

import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import com.dev.servlet.adapter.in.web.builder.HtmlTemplate;
import com.dev.servlet.adapter.in.web.builder.RequestBuilder;
import com.dev.servlet.adapter.in.web.dispatcher.HttpExecutor;
import com.dev.servlet.adapter.in.web.dispatcher.IServletDispatcher;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.adapter.in.web.dto.Request;
import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.port.in.user.UserDetailsPort;
import com.dev.servlet.application.port.out.audit.AuditPort;
import com.dev.servlet.application.port.out.security.AuthCookiePort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.response.UserResponse;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.RequestMethod;
import com.dev.servlet.infrastructure.utils.URIUtils;
import static com.dev.servlet.infrastructure.utils.URIUtils.matchWildcard;
import static com.dev.servlet.shared.enums.ConstantUtils.BEARER_PREFIX;
import com.dev.servlet.shared.util.CloneUtil;
import com.dev.servlet.shared.vo.AuditPayload;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Slf4j
@NoArgsConstructor
@RequestScoped
public class ServletDispatcherImpl implements IServletDispatcher {

    private static final List<String> noAuditEndpoints = List.of(
            "GET:/api/v1/inspect/*",
            "GET:/api/v1/health/*",
            "GET:/api/v1/activity/*",
            "GET:/api/v1/alert/*"
    );

    @Inject
    private AuthCookiePort authCookiePort;
    @Inject
    private UserDetailsPort userDetailsPort;
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private HttpExecutor<?> httpExecutor;
    @Inject
    private AuditPort auditPort;

    private static Request requestOf(HttpServletRequest httpServletRequest) {
        return RequestBuilder.newBuilder().servletRequest(httpServletRequest).complete().build();
    }

    @Interceptors({LogExecutionTimeInterceptor.class})
    public void dispatch(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
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

    private void handleRequestAttributes(HttpServletRequest httpRequest, IHttpResponse<?> response) {
        httpRequest.setAttribute("response", response);

        try {
            var queries = URIUtils.filterQueryParameters(httpRequest.getQueryString());
            if (queries != null) {
                httpRequest.setAttribute("q", queries.get("q"));
                httpRequest.setAttribute("k", queries.get("k"));
                httpRequest.setAttribute("ct", queries.get("ct"));
            }
        } catch (Exception ignored) {
        }

        for (var key : httpRequest.getParameterMap().keySet()) {
            // There are wrappers that ensure no XSS attack is possible via parameters
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
            Request request, IHttpResponse<?> response) {
        log.trace("");
        if (RequestMethod.POST.isEquals(request.getMethod())
                && response.body() instanceof UserResponse user && user.hasToken()) {
            authCookiePort.setAuthCookies(httpResponse, user.getToken(), user.getRefreshToken());
        }

        httpResponse.setHeader("X-Correlation-ID", MDC.get("correlationId"));

        logAuditEvent(request, response);
        attachUserToRequest(httpRequest);
        handleRequestAttributes(httpRequest, response);
        handleResponseErrors(httpRequest, httpResponse, response);

        if (request.contains("logout")) {
            authCookiePort.clearCookies(httpResponse);
        }
    }

    private void attachUserToRequest(HttpServletRequest httpRequest) {
        try {
            String token = authCookiePort.getCookieFromArray(httpRequest.getCookies(), authCookiePort.getAccessTokenCookieName());
            if (StringUtils.isNotBlank(token)) {
                User user = authenticationPort.extractUser(BEARER_PREFIX + token);
                UserResponse response = userDetailsPort.get(user.getId(), token);
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

    private void logAuditEvent(Request request, IHttpResponse<?> response) {
        final String eventName = "%s:%s".formatted(request.getMethod(), request.getEndpoint());

        if (noAuditEndpoints.stream().noneMatch(edp -> matchWildcard(edp, eventName))) {
            if (response.statusCode() >= 200 && response.statusCode() < 400) {
                auditSuccess(eventName, request, response);
            } else {
                auditPort.failure(eventName, request.getToken(), new AuditPayload<>(request, response));
            }
        }
    }

    private void auditSuccess(String eventName, Request request, IHttpResponse<?> response) {
        Object payload = CloneUtil.summarizeResponseBody(response.body());
        auditPort.success(eventName, request.getToken(), new AuditPayload<>(request, payload));
    }
}
