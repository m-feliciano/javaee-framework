package com.dev.servlet.adapter.in.web.frontcontroller;

import com.dev.servlet.adapter.in.web.builder.RequestBuilder;
import com.dev.servlet.adapter.in.web.dispatcher.IServletDispatcher;
import com.dev.servlet.adapter.in.web.dispatcher.impl.LogExecutionTimeInterceptor;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.adapter.in.web.dto.Request;
import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.port.out.audit.AuditPort;
import com.dev.servlet.application.port.out.security.AuthCookiePort;
import com.dev.servlet.application.transfer.response.UserResponse;
import com.dev.servlet.domain.entity.enums.RequestMethod;
import com.dev.servlet.infrastructure.utils.URIUtils;
import com.dev.servlet.shared.util.CloneUtil;
import com.dev.servlet.shared.vo.AuditPayload;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.stream.Stream;

import static com.dev.servlet.infrastructure.utils.URIUtils.matchWildcard;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1MB Max
        maxFileSize = 1024 * 1024,
        maxRequestSize = 1024 * 1024
)
@Slf4j
public class FrontControllerServlet extends HttpServlet {
    // Api endpoints that are excluded from audit logging
    private static final String GET_INSPECT_EVENT = "GET:/api/v1/inspect/*";
    private static final String GET_ACTIVITY_EVENT = "GET:/api/v1/activity/*";
    private static final String GET_HEALTH_UP_EVENT = "GET:/api/v1/health/up";
    private static final String GET_ALERT_EVENT = "GET:/api/v1/alert/*";

    @Inject
    private AuditPort auditPort;
    @Inject
    private AuthCookiePort authCookiePort;
    @Inject
    private IServletDispatcher dispatcher;
    @Inject
    private ResponseWriter responseWriter;
    @Inject
    private ErrorResponseWriter errorWriter;

    @Override
    @Interceptors({LogExecutionTimeInterceptor.class})
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.trace("service(req={}, resp={})", req, resp);

        final String event = "%s:%s".formatted(req.getMethod(), req.getRequestURI());
        log.debug("Processing request for event: {}", event);

        boolean logEndpoint = isLogEndpoint(event);

        Request request = null;
        IHttpResponse<?> response = null;
        try {
            request = RequestBuilder.newBuilder().servletRequest(req).complete().build();
            response = dispatcher.dispatch(request);

            if (response.error() != null && response.reasonText() == null) {
                log.warn("Unsuccessful response: {}", response.error());
                throw new AppException(response.statusCode(), response.error());
            }

            // todo: frontend responsibility to fetch user details? its cached though, so low impact
            if (request.getToken() != null) {
                Request userRequest = Request.builder()
                        .endpoint("/api/v1/user/me")
                        .method(RequestMethod.GET)
                        .token(request.getToken())
                        .build();

                IHttpResponse<?> userResponse = dispatcher.dispatch(userRequest);
                req.setAttribute("user", userResponse.body());
            }

            handleRequestAttributes(req, response);

            if (logEndpoint) handleCookies(resp, request, response);

            responseWriter.write(req, resp, request, response);

        } catch (AppException e) {
            log.warn("Application error processing request: {}", e.getMessage());

            try {
                errorWriter.write(req, resp, e.getCode(), e.getMessage());
            } catch (Exception ex) {
                log.error("Error writing application error response: {}", ex.getMessage(), ex);
            }

        } catch (Exception e) {
            log.error("Unexpected error processing request: {}", e.getMessage(), e);

            try {
                errorWriter.write(req, resp, 500, "Unexpected error");
            } catch (Exception ex) {
                log.error("Error writing unexpected error response: {}", ex.getMessage(), ex);
            }

        } finally {
            logAuditEvent(request, response, logEndpoint);
            log.debug("Completed processing request for event: {}", event);
        }
    }

    private static boolean isLogEndpoint(String event) {
        return Stream.of(GET_HEALTH_UP_EVENT,
                        GET_INSPECT_EVENT,
                        GET_ACTIVITY_EVENT,
                        GET_ALERT_EVENT)
                .noneMatch(pattern -> matchWildcard(pattern, event));
    }

    private void logAuditEvent(Request req, IHttpResponse<?> res, boolean logActivity) {
        if (req == null || !logActivity) return;

        String event = "%s:%s".formatted(req.getMethod(), req.getEndpoint());

        int status = res != null ? res.statusCode() : 500;
        if (status >= 200 && status < 400) {
            Object payload = CloneUtil.summarizeResponseBody(res.body());
            auditPort.success(event, req.getToken(), new AuditPayload<>(req, payload));
        } else {
            auditPort.failure(event, req.getToken(), new AuditPayload<>(req, res));
        }
    }

    private void handleRequestAttributes(HttpServletRequest req, IHttpResponse<?> response) {
        req.setAttribute("response", response);

        try {
            var queries = URIUtils.filterQueryParameters(req.getQueryString());
            if (queries != null) {
                // Set common query attributes for views
                req.setAttribute("q", queries.get("q"));
                req.setAttribute("k", queries.get("k"));
                req.setAttribute("ct", queries.get("category"));
            }
        } catch (Exception ignored) {
        }

        for (var key : req.getParameterMap().keySet()) {
            req.setAttribute(key, req.getParameter(key));
        }
    }

    private void handleCookies(HttpServletResponse resp, Request request, IHttpResponse<?> response) {
        if (RequestMethod.GET.equals(request.getMethod())) {
            authCookiePort.addCdnCookies(resp);
        }

        if (RequestMethod.POST.equals(request.getMethod())) {
            if (response.body() instanceof UserResponse user && user.hasToken()) {
                authCookiePort.setAuthCookies(resp, user.getToken(), user.getRefreshToken());
            }

            if (request.contains("logout")) {
                authCookiePort.clearCookies(resp);
            }
        }
    }
}
