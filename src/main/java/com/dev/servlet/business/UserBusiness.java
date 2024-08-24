package com.dev.servlet.business;

import com.dev.servlet.business.base.BaseRequest;
import com.dev.servlet.controllers.UserController;
import com.dev.servlet.domain.User;
import com.dev.servlet.domain.enums.PerfilEnum;
import com.dev.servlet.domain.enums.StatusEnum;
import com.dev.servlet.dto.UserDto;
import com.dev.servlet.filter.StandardRequest;
import com.dev.servlet.interfaces.ResourcePath;
import com.dev.servlet.mapper.UserMapper;
import com.dev.servlet.utils.CacheUtil;
import com.dev.servlet.utils.CryptoUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The type User business.
 * <p>
 * This class is responsible for the user business logic.
 *
 * @see BaseRequest
 */
@Singleton
public class UserBusiness extends BaseRequest {

    private static final String FORWARD_PAGE_CREATE = "forward:pages/user/formCreateUser.jsp";
    private static final String FORWARD_PAGE_LIST = "forward:pages/user/formListUser.jsp";
    private static final String FORWARD_PAGE_UPDATE = "forward:pages/user/formUpdateUser.jsp";
    private static final String REDIRECT_ACTION_LIST_BY_ID = "redirect:user?action=list&id=";
    private static final String REDIRECT_PRODUCT_ACTION_CREATE = "redirect:product?action=create";

    private UserController controller;

    public UserBusiness() {
        // Empty constructor
    }

    @Inject
    public void setDependencies(UserController controller) {
        this.controller = controller;
    }

    /**
     * Forward to create
     *
     * @return
     */
    @ResourcePath(value = REGISTER_PAGE)
    public String forwardRegister(StandardRequest request) {
        return FORWARD_PAGE_CREATE;
    }

    /**
     * Redirect to Edit user.
     *
     * @param standardRequest
     * @return the string
     */
    @ResourcePath(value = REGISTER)
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

        try {
            user.addPerfil(PerfilEnum.DEFAULT.cod);
            user.setStatus(StatusEnum.ACTIVE.getName());
            controller.save(user);
        } catch (Exception e) {
            standardRequest.servletRequest().setAttribute("error", e.getMessage());
            standardRequest.servletResponse().sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return REDIRECT_PRODUCT_ACTION_CREATE;
        }

        standardRequest.servletRequest().setAttribute("sucess", "sucess");
        standardRequest.servletResponse().setStatus(HttpServletResponse.SC_CREATED);
        return FORWARD_PAGES_FORM_LOGIN;
    }

    /**
     * Update user.
     *
     * @param request
     * @return the string
     */
    @ResourcePath(value = UPDATE)
    public String update(StandardRequest request) {
        UserDto dto = CacheUtil.getUser(request.token());
        User user = new User(dto.getId());
        user.setLogin(getParameter(request, "email").toLowerCase());
        user.setImgUrl(getParameter(request, "imgUrl"));
        user.setPassword(CryptoUtils.encrypt(getParameter(request, "password")));
        user.setPerfis(dto.getPerfis());
        user.setStatus(dto.getStatus());
        user = controller.update(user);

        UserDto userDto = UserMapper.from(user);
        CacheUtil.storeToken(request.token(), userDto);
        setSessionAttribute(request.servletRequest(), "user", userDto);
        request.servletResponse().setStatus(HttpServletResponse.SC_NO_CONTENT);
        return REDIRECT_ACTION_LIST_BY_ID + user.getId();
    }

    /**
     * List user by session.
     *
     * @param standardRequest
     * @return the string
     */
    @ResourcePath(value = LIST)
    public String findAll(StandardRequest standardRequest) {
        HttpServletRequest request = standardRequest.servletRequest();
        User user = getUser(standardRequest);
        request.setAttribute("user", UserMapper.from(user));
        return FORWARD_PAGE_LIST;
    }

    /**
     * Edit user.
     *
     * @param request
     * @return the string
     */
    @ResourcePath(value = EDIT)
    public String edit(StandardRequest request) {
        HttpServletRequest req = request.servletRequest();

        User user = controller.findById(Long.parseLong(getParameter(request, "id")));
        req.setAttribute("user", UserMapper.from(user));
        return FORWARD_PAGE_UPDATE;
    }

    /**
     * Delete one
     *
     * @param request
     * @return
     */
    @ResourcePath(value = DELETE)
    public String delete(StandardRequest request) {
        User user = getUser(request);
        controller.delete(user);
        CacheUtil.clearToken(request.token());
        request.servletResponse().setStatus(HttpServletResponse.SC_NO_CONTENT);
        return FORWARD_PAGES_FORM_LOGIN;
    }

}
