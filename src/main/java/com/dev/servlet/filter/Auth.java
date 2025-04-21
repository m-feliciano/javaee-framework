package com.dev.servlet.filter;

import com.dev.servlet.dto.UserDTO;
import com.dev.servlet.interfaces.IServletDispatcher;
import com.dev.servlet.utils.CryptoUtils;
import com.dev.servlet.utils.PropertiesUtil;
import com.dev.servlet.utils.URIUtils;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Authentication filter to check user authorization for accessing services.
 *
 * @since 1.0
 */
@Slf4j
public class Auth implements Filter {

    private static final List<String> AUTHORIZED_PATH = PropertiesUtil.getProperty("auth.authorized", List.of("login,user"));

    @Inject
    @Named("ServletDispatch") // No need to specify the name if the class has only one implementation
    private IServletDispatcher dispatcher;

    public Auth() {
        // Empty constructor
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        UserDTO user = (UserDTO) request.getSession().getAttribute("user");
        String token = user != null ? user.getToken() : null;

        if (isAuthorizedRequest(request, token)) {
            dispatcher.dispatch(request, response);
        } else {
            log.warn("Unauthorized access to the service: {}, redirecting to login page", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendRedirect(PropertiesUtil.getProperty("loginpage"));
        }
    }

    /**
     * Checks if the token is valid.
     *
     * @param token the session token
     * @return true if the token is valid, false otherwise
     */
    private boolean isValidToken(String token) {
        return token != null && CryptoUtils.verifyToken(token);
    }

    /**
     * Checks if the user is authorized to access the action.
     *
     * @param request the HTTP request
     * @return true if the action is authorized, false otherwise
     */
    private boolean isAuthorizedRequest(HttpServletRequest request, String token) {
        if (isValidToken(token)) {
            return true;
        }

        String service = URIUtils.getServicePath(request);
        String serviceName = URIUtils.getServiceName(request);

        if (serviceName == null && service == null) {
            return false;
        }

        return service != null && AUTHORIZED_PATH.contains(service);
    }
}