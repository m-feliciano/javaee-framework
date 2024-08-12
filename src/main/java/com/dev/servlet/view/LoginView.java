package com.dev.servlet.view;

import com.dev.servlet.controllers.UserController;
import com.dev.servlet.domain.User;
import com.dev.servlet.dto.UserDto;
import com.dev.servlet.filter.StandardRequest;
import com.dev.servlet.interfaces.ResourcePath;
import com.dev.servlet.mapper.UserMapper;
import com.dev.servlet.utils.CacheUtil;
import com.dev.servlet.utils.CryptoUtils;
import com.dev.servlet.view.base.BaseRequest;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

public class LoginView extends BaseRequest {

    private static final String REDIRECT_PRODUCT_ACTION_LIST_ALL = "redirect:productView?action=list";

    private UserController controller;


    public LoginView() {
    }

    public LoginView(EntityManager entityManager) {
        this.controller = new UserController(entityManager);
    }

    /**
     * Forward
     *
     * @return the next path
     */
    @ResourcePath(value = LOGIN_FORM, forward = true)
    public String forwardLogin() {
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
        return this.forwardLogin();
    }
}
