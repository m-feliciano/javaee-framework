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
import javax.servlet.http.HttpServletRequest;
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
     * @param standardRequest
     * @return the next path
     */
    @ResourcePath(value = LOGIN)
    public String login(StandardRequest standardRequest) {
        HttpServletRequest req = standardRequest.getRequest();

        if (getParameter(req, "sucess") != null) {
            return FORWARD_PAGES_FORM_LOGIN;
        }

        User user = new User();
        user.setLogin(getParameter(req, "email"));
        user.setPassword(CryptoUtils.encrypt(getParameter(req, "password")));
        user = controller.find(user);
        if (user == null) {
            req.setAttribute(INVALID, USER_OR_PASSWORD_INVALID);
            req.setAttribute("email", getParameter(req, "email"));
            return FORWARD_PAGES_FORM_LOGIN;
        }

        UserDto dto = UserMapper.from(user);

        setSessionAttribute(req, "token", CryptoUtils.generateToken(dto));
        setSessionAttribute(req, "user", dto);
        return REDIRECT_PRODUCT_ACTION_LIST_ALL;
    }

    /**
     * Logout.
     *
     * @param standardRequest
     * @return the next path
     */
    @ResourcePath(value = LOGOUT)
    public String logout(StandardRequest standardRequest) {
        HttpServletRequest req = standardRequest.getRequest();

        HttpSession session = req.getSession();
        CacheUtil.clearToken(getToken(req));
        session.invalidate();
        return this.forwardLogin();
    }
}
