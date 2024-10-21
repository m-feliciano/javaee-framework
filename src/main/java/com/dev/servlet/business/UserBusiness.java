package com.dev.servlet.business;

import com.dev.servlet.business.base.BaseRequest;
import com.dev.servlet.controllers.UserController;
import com.dev.servlet.dto.UserDto;
import com.dev.servlet.interfaces.IService;
import com.dev.servlet.interfaces.ResourcePath;
import com.dev.servlet.mapper.UserMapper;
import com.dev.servlet.pojo.User;
import com.dev.servlet.pojo.enums.PerfilEnum;
import com.dev.servlet.pojo.enums.StatusEnum;
import com.dev.servlet.pojo.records.StandardRequest;
import com.dev.servlet.utils.CacheUtil;
import com.dev.servlet.utils.CryptoUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * The type User business.
 * <p>
 * This class is responsible for the user business logic.
 *
 * @see BaseRequest
 */
@Singleton
@IService("user")
public class UserBusiness extends BaseRequest {
    public static final String FORWARD_PAGES_USER = "forward:pages/user/";
    public static final String FORWARD_PAGE_CREATE = FORWARD_PAGES_USER + "formCreateUser.jsp";

    private UserController controller;

    public UserBusiness() {
        // Empty constructor
    }

    @Inject
    public void setDependencies(UserController controller) {
        this.controller = controller;
    }

    /**
     * Redirect to Edit user.
     *
     * @param standardRequest
     * @return the string
     */
//    @ResourcePath(REGISTER)
    public String register(StandardRequest standardRequest) throws Exception {

        var password = getParameter(standardRequest, "password");
        var confirmPassword = getParameter(standardRequest, "confirmPassword");

        if (password == null || !password.equals(confirmPassword)) {
            standardRequest.servletRequest().setAttribute("email", getParameter(standardRequest, "email"));
            standardRequest.servletRequest().setAttribute("error", "password invalid");
            standardRequest.servletResponse().sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return FORWARD_PAGE_CREATE;
        }

        User user = new User();
        String email = getParameter(standardRequest, "email").toLowerCase();
        user.setLogin(email);
        user = controller.find(user);

        if (user != null) {
            standardRequest.servletRequest().setAttribute("error", "User already exists");
            standardRequest.servletResponse().sendError(HttpServletResponse.SC_FORBIDDEN);
            return FORWARD_PAGE_CREATE;
        }

        user = new User(email, CryptoUtils.encrypt(password));

        user.addPerfil(PerfilEnum.DEFAULT.cod);
        user.setStatus(StatusEnum.ACTIVE.value);
        controller.save(user);

        standardRequest.servletRequest().setAttribute("success", "success");
        standardRequest.servletResponse().setStatus(HttpServletResponse.SC_CREATED);
        return FORWARD_PAGES_FORM_LOGIN;
    }

    /**
     * Update user.
     *
     * @param request
     * @return the string
     */
    @ResourcePath(UPDATE)
    public String update(StandardRequest request) throws Exception {
        Long resourceId = request.requestObject().resourceId();
        if (resourceId == null) {
            request.servletResponse().sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        User cached = getUser(request);

        if (!Objects.equals(request.token(), cached.getToken())) {
            request.servletResponse().sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }

        User user = new User(resourceId);
        user.setLogin(getParameter(request, "email").toLowerCase());
        user.setImgUrl(getParameter(request, "imgUrl"));
        user.setPassword(CryptoUtils.encrypt(getParameter(request, "password")));
        user.setPerfis(cached.getPerfis());
        user.setStatus(cached.getStatus());
        user = controller.update(user);

        UserDto updated = UserMapper.from(user);
        CacheUtil.storeToken(request.token(), updated);

        setSessionAttribute(request.servletRequest(), "user", updated);
        request.servletResponse().setStatus(HttpServletResponse.SC_NO_CONTENT);
        return "redirect:/view/user/list/<id>".replace("<id>", user.getId().toString());
    }

    /**
     * List user by session.
     *
     * @param standardRequest
     * @return the string
     */
    @ResourcePath(LIST)
    public String find(StandardRequest standardRequest) {
        HttpServletRequest request = standardRequest.servletRequest();
        User user = getUser(standardRequest);
        request.setAttribute("user", UserMapper.from(user));
        return FORWARD_PAGES_USER + "formListUser.jsp";
    }

    /**
     * Edit user.
     *
     * @param request
     * @return the string
     */
    @ResourcePath(EDIT)
    public String edit(StandardRequest request) throws Exception {
        Long resourceId = request.requestObject().resourceId();
        if (resourceId == null) {
            request.servletResponse().sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        User cached = getUser(request);
        if (!Objects.equals(request.token(), cached.getToken())) {
            request.servletResponse().sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }

        User user = new User(resourceId);
        user = controller.find(user);

        request.servletRequest().setAttribute("user", UserMapper.from(user));
        return FORWARD_PAGES_USER + "formUpdateUser.jsp";
    }

    /**
     * Delete one
     *
     * @param request
     * @return
     */
    @ResourcePath(DELETE)
    public String delete(StandardRequest request) throws Exception {
        Long resourceId = request.requestObject().resourceId();
        if (resourceId == null) {
            request.servletResponse().sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        User cached = getUser(request);
        if (!Objects.equals(request.token(), cached.getToken())) {
            request.servletResponse().sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }

        controller.delete(cached);
        CacheUtil.clearToken(request.token());
        request.servletResponse().setStatus(HttpServletResponse.SC_NO_CONTENT);
        return FORWARD_PAGES_FORM_LOGIN;
    }

}
