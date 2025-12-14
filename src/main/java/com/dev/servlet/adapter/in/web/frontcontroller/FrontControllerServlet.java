package com.dev.servlet.adapter.in.web.frontcontroller;

import com.dev.servlet.adapter.in.web.builder.RequestBuilder;
import com.dev.servlet.adapter.in.web.dispatcher.IServletDispatcher;
import com.dev.servlet.adapter.in.web.dispatcher.impl.LogExecutionTimeInterceptor;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.adapter.in.web.dto.Request;
import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.port.out.audit.AuditPort;
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
import java.util.List;

import static com.dev.servlet.infrastructure.utils.URIUtils.matchWildcard;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1MB Max
        maxFileSize = 1024 * 1024,
        maxRequestSize = 1024 * 1024
)
@Slf4j
public class FrontControllerServlet extends HttpServlet {

    private static final List<String> noAuditEndpoints = List.of(
            "GET:/api/v1/inspect/*",
            "GET:/api/v1/health/*",
            "GET:/api/v1/activity/*",
            "GET:/api/v1/alert/*"
    );

    @Inject
    private AuditPort auditPort;
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

        Request request = null;
        try {
            request = RequestBuilder.newBuilder().servletRequest(req).complete().build();

            IHttpResponse<?> response = dispatcher.dispatch(request);

            resp.setStatus(response.statusCode());

            if (response.error() != null && response.reasonText() == null) {
                log.warn("Unsuccessful response: {}", response.error());
                throw new AppException(response.statusCode(), response.error());
            }

            responseWriter.write(req, resp, request, response);
            logAuditEvent(request, response);

        } catch (AppException e) {
            log.warn("Application error processing request: {}", e.getMessage());

            try {
                errorWriter.write(req, resp, e.getCode(), e.getMessage());
            } catch (Exception ex) {
                log.error("Error writing application error response: {}", ex.getMessage(), ex);
            }

            logAuditEvent(request, null);

        } catch (Exception e) {
            log.error("Unexpected error processing request: {}", e.getMessage(), e);

            try {
                errorWriter.write(req, resp, 500, "Unexpected error");
            } catch (Exception ex) {
                log.error("Error writing unexpected error response: {}", ex.getMessage(), ex);
            }

            logAuditEvent(request, null);
        }
    }

    private void logAuditEvent(Request req, IHttpResponse<?> res) {
        if (req == null) return;

        String eventName = "%s:%s".formatted(req.getMethod(), req.getEndpoint());

        if (noAuditEndpoints.stream().noneMatch(edp -> matchWildcard(edp, eventName))) {

            int status = res != null ? res.statusCode() : 500;
            if (status >= 200 && status < 400) {
                Object payload = CloneUtil.summarizeResponseBody(res.body());
                auditPort.success(eventName, req.getToken(), new AuditPayload<>(req, payload));
            } else {
                auditPort.failure(eventName, req.getToken(), new AuditPayload<>(req, res));
            }
        }
    }
}
