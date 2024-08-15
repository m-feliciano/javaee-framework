package com.dev.servlet.business;

import com.dev.servlet.controllers.UserController;
import com.dev.servlet.domain.User;
import com.dev.servlet.dto.UserDto;
import com.dev.servlet.filter.StandardRequest;
import com.dev.servlet.interfaces.ResourcePath;
import com.dev.servlet.mapper.UserMapper;
import com.dev.servlet.utils.CacheUtil;
import com.dev.servlet.utils.CryptoUtils;
import com.dev.servlet.business.base.BaseRequest;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpSession;

/**
 * The type Login business.
 * <p>
 * This class is responsible for the login business logic.
 *
 * @see BaseRequest
 * @since 1.0
 */
@Singleton
public class LoginBusiness extends BaseRequest {

    private static final String REDIRECT_PRODUCT_ACTION_LIST_ALL = "redirect:product?action=list";

    @Inject
    UserController controller;

    public LoginBusiness() {
    }

    public LoginBusiness(UserController controller) {
        this.controller = controller;
    }

    /**
     * Forward
     *
     * @return the next path
     */
    @ResourcePath(value = LOGIN_FORM)
    public String forwardLogin(StandardRequest request) {
        return FORWARD_PAGES_FORM_LOGIN;
    }

    /**
     * Login.
     *
     * @param request
     * @return the next path
     */
    @ResourcePath(value = LOGIN)
    public String login(StandardRequest request) {
        if (getParameter(request, "sucess") != null) {
            return FORWARD_PAGES_FORM_LOGIN;
        }

        User user = new User();
        user.setLogin(getParameter(request, "email"));
        user.setPassword(CryptoUtils.encrypt(getParameter(request, "password")));
        user = controller.find(user);
        if (user == null) {
            request.servletRequest().setAttribute(INVALID, USER_OR_PASSWORD_INVALID);
            request.servletRequest().setAttribute("email", getParameter(request, "email"));
            return FORWARD_PAGES_FORM_LOGIN;
        }

        UserDto dto = UserMapper.from(user);

        setSessionAttribute(request.servletRequest(), "token", CryptoUtils.generateToken(dto));
        setSessionAttribute(request.servletRequest(), "user", dto);
        return REDIRECT_PRODUCT_ACTION_LIST_ALL;
    }

    /**
     * Logout.
     *
     * @param request
     * @return the next path
     */
    @ResourcePath(value = LOGOUT)
    public String logout(StandardRequest request) {
        HttpSession session = request.servletRequest().getSession();
        CacheUtil.clearToken(request.token());
        session.invalidate();
        return this.forwardLogin(request);
    }
}
