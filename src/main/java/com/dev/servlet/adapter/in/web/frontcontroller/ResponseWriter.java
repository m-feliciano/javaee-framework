package com.dev.servlet.adapter.in.web.frontcontroller;

import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.adapter.in.web.dto.Request;
import com.dev.servlet.application.port.in.user.UserDetailsPort;
import com.dev.servlet.application.port.out.security.AuthCookiePort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.response.UserResponse;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.RequestMethod;
import com.dev.servlet.infrastructure.utils.URIUtils;
import com.dev.servlet.shared.util.CloneUtil;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.io.PrintWriter;

import static com.dev.servlet.shared.enums.ConstantUtils.BEARER_PREFIX;

@Slf4j
@RequestScoped
public class ResponseWriter {
    @Inject
    private AuthCookiePort authCookiePort;
    @Inject
    private UserDetailsPort userDetailsPort;
    @Inject
    private AuthenticationPort authenticationPort;

    public void write(HttpServletRequest req,
                      HttpServletResponse resp,
                      Request request,
                      IHttpResponse<?> response) throws Exception {

        log.trace("write(req={}, resp={}, request={}, response={})", req, resp, request, response);

        attachUser(req);
        handleCookies(resp, request, response);
        handlerResponseData(req, resp, request, response);

        if (response.json()) {
            writeJson(resp, response);
        } else {
            handleNavigation(req, resp, response);
        }
    }

    private void handleRequestAttributes(HttpServletRequest req, IHttpResponse<?> response) {
        req.setAttribute("response", response);

        try {
            var queries = URIUtils.filterQueryParameters(req.getQueryString());
            if (queries != null) {
                req.setAttribute("q", queries.get("q"));
                req.setAttribute("k", queries.get("k"));
            }
        } catch (Exception ignored) {
        }

        for (var key : req.getParameterMap().keySet()) {
            req.setAttribute(key, req.getParameter(key));
        }
    }

    private void handlerResponseData(HttpServletRequest req, HttpServletResponse resp, Request request, IHttpResponse<?> response) {
        log.trace("Processing response data for request: {}", request.getEndpoint());

        resp.setHeader("X-Correlation-ID", MDC.get("correlationId"));
        handleRequestAttributes(req, response);

        log.debug("Response status code: {}", response.statusCode());
    }

    private void handleCookies(HttpServletResponse resp, Request request, IHttpResponse<?> response) {
        if (RequestMethod.POST.isEquals(request.getMethod())
            && response.body() instanceof UserResponse user && user.hasToken()) {
            authCookiePort.setAuthCookies(resp, user.getToken(), user.getRefreshToken());
        }

        if (request.contains("logout")) {
            authCookiePort.clearCookies(resp);
        } else {
            // Ensure CDN cookies are added: e.g., for Cloudflare
            authCookiePort.addCdnCookies(resp);
        }
    }

    private void writeJson(HttpServletResponse resp, IHttpResponse<?> response) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String json = CloneUtil.toJson(response.body());

        try (PrintWriter writer = resp.getWriter()) {
            writer.write(String.valueOf(json));
            writer.flush();
        } catch (Exception e) {
            String message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            log.error("Error writing JSON response: {}", message);
        }
    }

    private void handleNavigation(HttpServletRequest req, HttpServletResponse resp, IHttpResponse<?> response) throws Exception {
        if (response.next() == null) return;

        String[] path = response.next().split(":");
        if (path.length != 2) throw new Exception("Cannot parse URL: " + response.next());

        String pathAction = path[0];
        String pathUrl = path[1];

        if ("forward".equalsIgnoreCase(pathAction)) {
            req.getRequestDispatcher("/WEB-INF/view/" + pathUrl).forward(req, resp);
        } else {
            resp.sendRedirect(pathUrl);
        }
    }

    private void attachUser(HttpServletRequest req) {
        try {
            String token = authCookiePort.getCookieFromArray(req.getCookies(), authCookiePort.getAccessTokenCookieName());
            if (StringUtils.isNotBlank(token)) {
                User user = authenticationPort.extractUser(BEARER_PREFIX + token);
                UserResponse response = userDetailsPort.getDetail(user.getId(), token);
                req.setAttribute("user", response);
            }
        } catch (Exception e) {
            log.warn("Unable to add user to request", e);
        }
    }
}