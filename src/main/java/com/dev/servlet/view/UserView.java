package com.dev.servlet.view;

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
import com.dev.servlet.view.base.BaseRequest;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;

@Singleton
public class UserView extends BaseRequest {

    private static final String FORWARD_PAGE_CREATE = "forward:pages/user/formCreateUser.jsp";
    private static final String FORWARD_PAGE_LIST = "forward:pages/user/formListUser.jsp";
    private static final String FORWARD_PAGE_UPDATE = "forward:pages/user/formUpdateUser.jsp";
    private static final String REDIRECT_ACTION_LIST_BY_ID = "redirect:userView?action=list&id=";

    private static final String REDIRECT_PRODUCT_ACTION_CREATE = "redirect:productView?action=create";

    @Inject
    private UserController controller;

    public UserView() {
    }

    public UserView(UserController controller) {
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
    public String register(StandardRequest standardRequest) {
        HttpServletRequest request = standardRequest.servletRequest();

        var password = getParameter(standardRequest, "password");
        var confirmPassword = getParameter(standardRequest, "confirmPassword");

        if (password == null || !password.equals(confirmPassword)) {
            request.setAttribute("email", getParameter(standardRequest, "email"));
            request.setAttribute("error", "password invalid");
            return FORWARD_PAGE_CREATE;
        }

        User user = new User();
        String email = getParameter(standardRequest, "email").toLowerCase();
        user.setLogin(email);
        user = controller.find(user);

        if (user != null) {
            request.setAttribute("error", "User already exists");
            return FORWARD_PAGE_CREATE;
        }

        user = new User(email, CryptoUtils.encrypt(password));

        try {
            user.addPerfil(PerfilEnum.DEFAULT.cod);
            user.setStatus(StatusEnum.ACTIVE.getName());
            controller.save(user);
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            return REDIRECT_PRODUCT_ACTION_CREATE;
        }

        request.setAttribute("sucess", "sucess");
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
        return FORWARD_PAGES_FORM_LOGIN;
    }

}
