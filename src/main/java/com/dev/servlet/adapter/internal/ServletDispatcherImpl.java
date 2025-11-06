package com.dev.servlet.adapter.internal;

import com.dev.servlet.adapter.IHttpExecutor;
import com.dev.servlet.adapter.IServletDispatcher;
import com.dev.servlet.core.builder.HtmlTemplate;
import com.dev.servlet.core.builder.RequestBuilder;
import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.interfaces.IRateLimiter;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.core.util.JwtUtil;
import com.dev.servlet.core.util.PropertiesUtil;
import com.dev.servlet.core.util.URIUtils;
import com.dev.servlet.domain.model.User;
import com.dev.servlet.domain.model.enums.RequestMethod;
import com.dev.servlet.domain.service.AuthCookieService;
import com.dev.servlet.domain.service.IUserService;
import com.dev.servlet.domain.transfer.Request;
import com.dev.servlet.domain.transfer.request.UserRequest;
import com.dev.servlet.domain.transfer.response.UserResponse;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.text.MessageFormat;

@Setter
@Slf4j
@NoArgsConstructor
@ApplicationScoped
public class ServletDispatcherImpl implements IServletDispatcher {
    public static final int WAIT_TIME = 600;
    public static final String LOGOUT = "logout";

    private boolean rateLimitEnabled;

    @Inject
    private AuthCookieService cookieService;
    @Inject
    private IUserService userService;
    @Inject
    private JwtUtil jwts;
    @Inject
    private IHttpExecutor<?> httpExecutor;
    @Inject
    private IRateLimiter rateLimiter;

    @PostConstruct
    public void init() {
        rateLimitEnabled = PropertiesUtil.getProperty("rate.limit.enabled", true);
    }

    @Interceptors({LogExecutionTimeInterceptor.class})
    public void dispatch(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        this.execute(servletRequest, servletResponse);
    }

    private static Request newRequest(HttpServletRequest httpServletRequest) {
        return RequestBuilder.newBuilder().httpServletRequest(httpServletRequest).complete().retry(1).build();
    }

    private void execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String requestURI = httpServletRequest.getRequestURI();
        String method = httpServletRequest.getMethod();
        log.info("Processing request: {} {}", method, requestURI);

        try {
            if (rateLimitEnabled && !rateLimiter.acquireOrWait(WAIT_TIME)) {
                log.warn("Rate limit exceeded for request: {} {}", method, requestURI);
                throw new ServiceException(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Please try again later.");
            }

            Request request = newRequest(httpServletRequest);
            log.debug("Request object created: endpoint={}, method={}", request.getEndpoint(), request.getMethod());

            IHttpResponse<?> httpResponse = httpExecutor.call(request);
            log.debug("Response received: status={}, next={}", httpResponse.statusCode(), httpResponse.next());

            processResponse(httpServletRequest, httpServletResponse, request, httpResponse);

        } catch (ServiceException e) {
            log.error("Service exception for {} {}", method, requestURI, e);
            writeResponseError(httpServletRequest, httpServletResponse, e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected exception for {} {}", method, requestURI, e);
            String message = "An unexpected error occurred. Please try again later.";
            writeResponseError(httpServletRequest, httpServletResponse, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
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
                                     Request request, IHttpResponse<?> response) {
        if (RequestMethod.POST.getMethod().equals(request.getMethod()) && response.body() instanceof UserResponse userResponse) {
            if (userResponse.getToken() != null || userResponse.getRefreshToken() != null) {
                cookieService.setAuthCookies(httpResponse, userResponse.getToken(), userResponse.getRefreshToken());
                log.debug("✅ Auth cookies set for user ID: {}", userResponse.getId());
            }
        }

        addUserToRequest(httpRequest);
        setRequestAttributes(httpRequest, response);
        handleResponseErrors(httpRequest, httpResponse, response);

        if (request.getEndpoint() != null && request.getEndpoint().contains(LOGOUT)) {
            cookieService.clearAuthCookies(httpResponse);
            log.debug("✅ Cookies cleared on logout");
        }
    }

    private void addUserToRequest(HttpServletRequest httpRequest) {
        try {
            String token = cookieService.getTokenFromCookie(httpRequest, cookieService.getAccessTokenCookieName());
            if (StringUtils.isNotBlank(token)) {
                User user = jwts.getUser("Bearer " + token);
                UserResponse response = userService.getById(new UserRequest(user.getId()), token);
                httpRequest.setAttribute("user", response);
            }
        } catch (Exception e) {
            log.warn("⚠️ Unable to add user to request", e);
        }
    }

    private void processResponse(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Request request, IHttpResponse<?> response) throws ServiceException {
        processResponseData(httpRequest, httpResponse, request, response);
        if (response.next() == null) return;
        String[] path = response.next().split(":");
        if (path.length != 2) {
            throw new ServiceException("Cannot parse URL: " + response.next());
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
            throw new ServiceException("Error processing httpRequest: " + message);
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
