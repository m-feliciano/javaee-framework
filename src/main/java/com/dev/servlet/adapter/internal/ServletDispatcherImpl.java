package com.dev.servlet.adapter.internal;

import com.dev.servlet.adapter.HttpExecutor;
import com.dev.servlet.adapter.IServletDispatcher;
import com.dev.servlet.core.builder.HtmlTemplate;
import com.dev.servlet.core.builder.RequestBuilder;
import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.core.util.JwtUtil;
import com.dev.servlet.core.util.URIUtils;
import com.dev.servlet.domain.model.User;
import com.dev.servlet.domain.model.enums.RequestMethod;
import com.dev.servlet.service.AuthCookieService;
import com.dev.servlet.service.IUserService;
import com.dev.servlet.domain.request.Request;
import com.dev.servlet.domain.request.UserRequest;
import com.dev.servlet.domain.response.UserResponse;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.text.MessageFormat;

import static com.dev.servlet.core.enums.ConstantUtils.BEARER_PREFIX;

@Setter
@Slf4j
@NoArgsConstructor
@ApplicationScoped
public class ServletDispatcherImpl implements IServletDispatcher {
    public static final String LOGOUT = "logout";

    @Inject
    private AuthCookieService cookieService;
    @Inject
    private IUserService userService;
    @Inject
    private JwtUtil jwts;
    @Inject
    private HttpExecutor<?> httpExecutor;

    @Interceptors({LogExecutionTimeInterceptor.class})
    public void dispatch(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        this.execute(servletRequest, servletResponse);
    }

    private static Request requestOf(HttpServletRequest httpServletRequest) {
        return RequestBuilder.newBuilder().servletRequest(httpServletRequest).complete().retry(1).build();
    }

    private void execute(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        String requestURI = servletRequest.getRequestURI();
        String method = servletRequest.getMethod();
        log.info("Processing request: {} {}", method, requestURI);

        try {
            Request request = requestOf(servletRequest);
            IHttpResponse<?> httpResponse = httpExecutor.send(request);
            processResponse(servletRequest, servletResponse, request, httpResponse);

        } catch (ServiceException e) {
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
                cookieService.setAuthCookies(httpResponse, user.getToken(), user.getRefreshToken());
            }
        }

        httpResponse.setHeader("X-Correlation-ID", MDC.get("correlationId"));

        addUserToRequest(httpRequest);
        setRequestAttributes(httpRequest, response);
        handleResponseErrors(httpRequest, httpResponse, response);

        if (request.contains(LOGOUT)) {
            cookieService.clearCookies(httpResponse);
        }
    }

    private void addUserToRequest(HttpServletRequest httpRequest) {
        try {
            String token = cookieService.getTokenFromCookie(httpRequest, cookieService.getAccessTokenCookieName());
            if (StringUtils.isNotBlank(token)) {
                User user = jwts.getUser(BEARER_PREFIX + token);
                UserResponse response = userService.getById(new UserRequest(user.getId()), token);
                httpRequest.setAttribute("user", response);
            }
        } catch (Exception e) {
            log.warn("Unable to add user to request", e);
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
