package com.dev.servlet.business;

import com.dev.servlet.business.base.BaseRequest;
import com.dev.servlet.controllers.UserController;
import com.dev.servlet.dto.ServiceException;
import com.dev.servlet.dto.UserDto;
import com.dev.servlet.interfaces.IService;
import com.dev.servlet.interfaces.ResourcePath;
import com.dev.servlet.mapper.UserMapper;
import com.dev.servlet.pojo.User;
import com.dev.servlet.pojo.records.StandardRequest;
import com.dev.servlet.utils.CacheUtil;
import com.dev.servlet.utils.CryptoUtils;
import com.dev.servlet.utils.PropertiesUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.dev.servlet.business.UserBusiness.FORWARD_PAGE_CREATE;

/**
 * The type Login business.
 * <p>
 * This class is responsible for the login business logic.
 *
 * @see BaseRequest
 * @since 1.0
 */
@Singleton
@IService("login")
public class LoginBusiness extends BaseRequest {

    protected static final String LOGIN = "login";
    protected static final String LOGOUT = "logout";
    protected static final String LOGIN_FORM = "loginForm";

    private UserController controller;
    private UserBusiness userBusiness;

    public LoginBusiness() {
        // Empty constructor
    }

    @Inject
    public void setDependencies(UserController controller,
                                UserBusiness userBusiness) {
        this.controller = controller;
        this.userBusiness = userBusiness;
    }

    /**
     * Forward
     *
     * @return the next path
     */
    @ResourcePath(LOGIN_FORM)
    public String forwardLogin(StandardRequest request) throws IOException {
        if (CryptoUtils.verifyToken(request.getToken())) {
            String homepage = PropertiesUtil.getProperty("homepage");
            request.setStatus(HttpServletResponse.SC_OK);
            return "redirect:/view" + homepage;
        }

        return FORWARD_PAGES_FORM_LOGIN;
    }

    /**
     * Login.
     *
     * @param request
     * @return the next path
     */
    @ResourcePath(LOGIN)
    public String login(StandardRequest request) throws IOException {
        if (request.getParameter("success") != null) {
            return FORWARD_PAGES_FORM_LOGIN;
        }

        User user = new User();
        user.setLogin(request.getParameter("email"));
        user.setPassword(CryptoUtils.encrypt(request.getParameter("password")));
        user = controller.find(user);
        if (user == null) {
            request.setAttribute(INVALID, "User or password invalid.");
            request.setAttribute("email", request.getParameter("email"));
            request.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return FORWARD_PAGES_FORM_LOGIN;
        }

        UserDto dto = UserMapper.from(user);

        String jwtToken = CryptoUtils.generateJWTToken(user);
        request.setSessionAttribute("token", jwtToken);
        request.setSessionAttribute("user", dto);

        // Force redirect to homepage
        String homepage = PropertiesUtil.getProperty("homepage");
        request.setStatus(HttpServletResponse.SC_OK);
        return "redirect:/view" + homepage;
    }

    /**
     * Forward to create
     *
     * @return
     */
    @ResourcePath(REGISTER_PAGE)
    public String forwardRegister(StandardRequest request) {
        return FORWARD_PAGE_CREATE;
    }

    /**
     * Register a new user
     *
     * @param request
     * @return
     * @throws Exception
     */
    @ResourcePath(REGISTER)
    public String register(StandardRequest request) throws Exception, ServiceException {
        return this.userBusiness.register(request);
    }

    /**
     * Logout.
     *
     * @param request
     * @return the next path
     */
    @ResourcePath(LOGOUT)
    public String logout(StandardRequest request) throws Exception {
        HttpSession session = request.getSession();
        CacheUtil.clearAll(request.getToken());
        session.invalidate();
        return this.forwardLogin(request);
    }
}
