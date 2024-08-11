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

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

public class UserView extends BaseRequest {

    private static final String FORWARD_PAGE_CREATE = "forward:pages/user/formCreateUser.jsp";
    private static final String FORWARD_PAGE_LIST = "forward:pages/user/formListUser.jsp";
    private static final String FORWARD_PAGE_UPDATE = "forward:pages/user/formUpdateUser.jsp";
    private static final String REDIRECT_ACTION_LIST_BY_ID = "redirect:userView?action=list&id=";

    private static final String REDIRECT_PRODUCT_ACTION_CREATE = "redirect:productView?action=create";

    private UserController controller;

    public UserView() {
        super();
    }

    public UserView(EntityManager em) {
        super();
        controller = new UserController(em);
    }

    /**
     * Forward to create
     *
     * @return
     */
    @ResourcePath(value = NEW, forward = true)
    public String forwardRegister() {
        return FORWARD_PAGE_CREATE;
    }

    /**
     * Redirect to Edit user.
     *
     * @param standardRequest
     * @return the string
     */
    @ResourcePath(value = CREATE)
    public String register(StandardRequest standardRequest) {
        HttpServletRequest request = standardRequest.getRequest();

        var password = getParameter(request, "password");
        var confirmPassword = getParameter(request, "confirmPassword");

        if (password == null || !password.equals(confirmPassword)) {
            request.setAttribute("email", getParameter(request, "email"));
            request.setAttribute("error", "password invalid");
            return FORWARD_PAGE_CREATE;
        }

        User user = new User();
        String email = getParameter(request, "email").toLowerCase();
        user.setLogin(email);
        user = controller.find(user);

        if (user != null) {
            request.setAttribute("error", "User already exists");
            return FORWARD_PAGE_CREATE;
        }

        user = new User(email, CryptoUtils.encrypt(password));

        try {
            user.addPerfil(PerfilEnum.DEFAULT.cod);
            user.setStatus(StatusEnum.ACTIVE.getDescription());
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
     * @param standardRequest
     * @return the string
     */
    @ResourcePath(value = UPDATE)
    public String update(StandardRequest standardRequest) {
        HttpServletRequest req = standardRequest.getRequest();
        String token = getToken(req);

        UserDto dto = CacheUtil.getUser(token);
        User user = new User(dto.getId());
        user.setLogin(getParameter(req, "email").toLowerCase());
        user.setImgUrl(getParameter(req, "imgUrl"));
        user.setPassword(CryptoUtils.encrypt(getParameter(req, "password")));
        user.setPerfis(dto.getPerfis());
        user.setStatus(dto.getStatus());
        controller.update(user);

        UserDto userDto = UserMapper.from(user);
        CacheUtil.storeToken(token, userDto);
        setSessionAttribute(req, "user", userDto);
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
        HttpServletRequest request = standardRequest.getRequest();
        User user = getUser(request);
        request.setAttribute("user", UserMapper.from(user));
        return FORWARD_PAGE_LIST;
    }

    /**
     * Edit user.
     *
     * @param standardRequest
     * @return the string
     */
    @ResourcePath(value = EDIT)
    public String edit(StandardRequest standardRequest) {
        HttpServletRequest req = standardRequest.getRequest();

        User user = controller.findById(Long.parseLong(getParameter(req, "id")));
        req.setAttribute("user", UserMapper.from(user));
        return FORWARD_PAGE_UPDATE;
    }

    /**
     * Delete one
     *
     * @param standardRequest
     * @return
     */
    @ResourcePath(value = DELETE)
    public String delete(StandardRequest standardRequest) {
        HttpServletRequest request = standardRequest.getRequest();
        User user = getUser(request);
        controller.delete(user);
        return FORWARD_PAGES_FORM_LOGIN;
    }

}
