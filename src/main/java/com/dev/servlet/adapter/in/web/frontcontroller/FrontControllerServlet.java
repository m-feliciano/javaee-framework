package com.dev.servlet.adapter.in.web.frontcontroller;

import com.dev.servlet.adapter.in.web.builder.RequestBuilder;
import com.dev.servlet.adapter.in.web.dispatcher.IServletDispatcher;
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
    private static final String GET_PROFILE_EVENT = "GET:/api/v1/user/profile";

    @Inject
    private AuditPort audit;
    @Inject
    private AuthCookiePort authCookie;
    @Inject
    private IServletDispatcher dispatcher;
    @Inject
    private ResponseWriter responseWriter;
    @Inject
    private ErrorResponseWriter errorWriter;

    private static boolean isUserRequest(String event) {
        return Stream.of(GET_HEALTH_UP_EVENT,
                        GET_INSPECT_EVENT,
                        GET_ACTIVITY_EVENT,
                        GET_ALERT_EVENT,
                        GET_PROFILE_EVENT
                )
                .noneMatch(pattern -> matchWildcard(pattern, event));
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.trace("service(req={}, resp={})", req, resp);

        final String event = "%s:%s".formatted(req.getMethod(), req.getRequestURI());
        log.debug("Processing request for event: {}", event);

        boolean isUserRequest = isUserRequest(event);

        Request input = null;
        IHttpResponse<?> output = null;
        try {
            input = RequestBuilder.newBuilder().servletRequest(req).complete().build();
            output = dispatcher.dispatch(input);

            if (output.error() != null && output.reasonText() == null) {
                log.warn("Unsuccessful response: {}", output.error());
                throw new AppException(output.statusCode(), output.error());
            }

            handleRequestAttributes(req, output);

            if (isUserRequest) handleCookies(resp, input, output);

            responseWriter.write(req, resp, input, output);

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
            if (input != null && isUserRequest) logAuditEvent(event, input, output);
            log.debug("Completed processing request for event: {}", event);
        }
    }

    private <T> void logAuditEvent(String event, Request input, IHttpResponse<T> resp) {
        IHttpResponse<?> output = CloneUtil.cloneResponseSummary(resp);
        AuditPayload<?, ?> payload = new AuditPayload<>(input, output);

        int status = resp != null ? resp.statusCode() : 500;
        if (status >= 200 && status < 400) {
            audit.success(event, input.getToken(), payload);
        } else {
            audit.failure(event, input.getToken(), payload);
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
            authCookie.addCdnCookies(resp);
        }

        if (RequestMethod.POST.equals(request.getMethod())) {
            if (response.body() instanceof UserResponse user && user.hasToken()) {
                authCookie.setAuthCookies(resp, user.getToken(), user.getRefreshToken());
            }

            if (request.contains("logout")) {
                authCookie.clearCookies(resp);
            }
        }
    }
}
