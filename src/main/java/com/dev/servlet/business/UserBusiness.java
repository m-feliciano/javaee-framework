package com.dev.servlet.business;

import com.dev.servlet.business.base.BaseRequest;
import com.dev.servlet.controllers.UserController;
import com.dev.servlet.dto.ServiceException;
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
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
     * @param request
     * @return the string
     */
//    @ResourcePath(REGISTER)
    public String register(StandardRequest request) throws ServiceException, IOException {

        var password = request.getParameter("password");
        var confirmPassword = request.getParameter("confirmPassword");

        if (password == null || !password.equals(confirmPassword)) {
            request.setAttribute("email", request.getParameter("email"));
            request.setAttribute("error", "password invalid");
            request.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return FORWARD_PAGE_CREATE;
        }

        User user = new User();
        String email = request.getRequiredParameter("email").toLowerCase();
        user.setLogin(email);
        user = controller.find(user);

        if (user != null) {
            request.setAttribute("error", "User already exists");
            request.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return FORWARD_PAGE_CREATE;
        }

        user = new User(email, CryptoUtils.encrypt(password));

        user.addPerfil(PerfilEnum.DEFAULT.cod);
        user.setStatus(StatusEnum.ACTIVE.value);
        controller.save(user);

        request.setAttribute("success", "success");
        request.setStatus(HttpServletResponse.SC_CREATED);
        return FORWARD_PAGES_FORM_LOGIN;
    }

    /**
     * Update user.
     *
     * @param request
     * @return the string
     */
    @ResourcePath(UPDATE)
    public String update(StandardRequest request) throws ServiceException {
        User userCache = getUserFromCache(request);

        User user = new User(userCache.getId());
        user.setLogin(request.getRequiredParameter("email").toLowerCase());
        user.setImgUrl(request.getParameter("imgUrl"));
        user.setPassword(CryptoUtils.encrypt(request.getRequiredParameter("password")));
        user.setPerfis(userCache.getPerfis());
        user.setStatus(StatusEnum.ACTIVE.value);
        user = controller.update(user);

        UserDto updated = UserMapper.from(user);

        // the roles may have changed, so we need to clear the cache and generate a new token
        CacheUtil.clearAll(request.getToken());

        String jwtToken = CryptoUtils.generateJWTToken(user);
        request.setSessionAttribute("token", jwtToken);
        request.setSessionAttribute("user", updated);

        request.setStatus(HttpServletResponse.SC_NO_CONTENT);
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
        User user = getUser(standardRequest);
        standardRequest.setAttribute("user", UserMapper.from(user));
        return FORWARD_PAGES_USER + "formListUser.jsp";
    }

    /**
     * Edit user.
     *
     * @param request
     * @return the string
     */
    @ResourcePath(EDIT)
    public String edit(StandardRequest request) throws ServiceException {
        User userCache = getUserFromCache(request);
        User user = new User(userCache.getId());
        user = controller.find(user);

        request.setAttribute("user", UserMapper.from(user));
        return FORWARD_PAGES_USER + "formUpdateUser.jsp";
    }

    /**
     * Delete one
     *
     * @param request
     * @return
     */
    @ResourcePath(DELETE)
    public String delete(StandardRequest request) throws ServiceException {
        User cached = getUserFromCache(request);
        controller.delete(cached);
        CacheUtil.clearAll(request.getToken());
        request.setStatus(HttpServletResponse.SC_NO_CONTENT);
        return FORWARD_PAGES_FORM_LOGIN;
    }

    /**
     * get user from cache
     *
     * @param request
     * @return the string
     */
    private User getUserFromCache(StandardRequest request) throws ServiceException {
        if (request.getId() == null) throwResourceNotFoundException(null);

        User cached = getUser(request);

        if (!Objects.equals(cached.getId(), request.getId())) {
            throw new ServiceException(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }

        if (!Objects.equals(request.getToken(), cached.getToken())) {
            throw new ServiceException(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }

        return cached;
    }

}
