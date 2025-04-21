package com.dev.servlet.controllers;

import com.dev.servlet.dto.ServiceException;
import com.dev.servlet.dto.UserDTO;
import com.dev.servlet.interfaces.Constraints;
import com.dev.servlet.interfaces.Controller;
import com.dev.servlet.interfaces.IHttpResponse;
import com.dev.servlet.interfaces.RequestMapping;
import com.dev.servlet.interfaces.Validator;
import com.dev.servlet.model.LoginModel;
import com.dev.servlet.pojo.domain.User;
import com.dev.servlet.pojo.enums.RequestMethod;
import com.dev.servlet.pojo.records.HttpResponse;
import com.dev.servlet.pojo.records.Request;
import com.dev.servlet.utils.CryptoUtils;
import com.dev.servlet.utils.PropertiesUtil;
import lombok.NoArgsConstructor;

import javax.inject.Inject;


@NoArgsConstructor
@Controller(path = "/login")
public final class LoginController extends BaseController<User, Long> {

    private static final String HOMEPAGE = PropertiesUtil.getProperty("homepage");
    private static final String FORWARD_PAGES_FORM_LOGIN_JSP = "forward:pages/formLogin.jsp";
    private static final String FORWARD_PAGES_USER_FORM_CREATE_USER_JSP = "forward:pages/user/formCreateUser.jsp";

    @Inject
    public LoginController(LoginModel userModel) {
        super(userModel);
    }

    private LoginModel getModel() {
        return (LoginModel) super.getBaseModel();
    }

    /**
     * Forward to register page
     *
     * @return {@linkplain IHttpResponse}
     */
    @RequestMapping(value = "/registerPage", requestAuth = false)
    public IHttpResponse<String> forwardRegister() {
        return HttpResponse.ofNext(FORWARD_PAGES_USER_FORM_CREATE_USER_JSP);
    }

    /**
     * Login user.
     *
     * @param request {@linkplain Request}
     * @return {@linkplain IHttpResponse} with the updated user
     */
    @RequestMapping(value = "/form", requestAuth = false)
    public IHttpResponse<String> form(Request request) {

        if (CryptoUtils.verifyToken(request.token())) {
            return HttpResponse.ofNext("redirect:/" + HOMEPAGE);
        }

        return HttpResponse.ofNext(FORWARD_PAGES_FORM_LOGIN_JSP);
    }

    /**
     * Login user.
     *
     * @param request {@linkplain Request}
     * @return {@linkplain IHttpResponse} with the updated user
     * @throws ServiceException if the user is not found
     */
    @RequestMapping(
            value = "/login",
            method = RequestMethod.POST,
            requestAuth = false,
            validators = {
                    @Validator(values = "login", constraints = {
                            @Constraints(isEmail = true, message = "Email must be valid"),
                    }),
                    @Validator(values = "password", constraints = {
                            @Constraints(minLength = 5, maxLength = 30, message = "Password must have at least {0} characters")
                    })
            })
    public IHttpResponse<UserDTO> login(Request request) throws ServiceException {
        UserDTO user = getModel().login(request);
        // OK
        return super.okHttpResponse(user, "redirect:/" + HOMEPAGE);
    }

    /**
     * Logout user.
     *
     * @param request {@linkplain Request}
     * @return {@linkplain IHttpResponse} with the updated user
     */
    @RequestMapping(
            value = "/logout",
            method = RequestMethod.POST
//            requestAuth = true,
//            requestParams = {
//                    @Validator(value = "token", constraints = {
//                            @Constraints(notNullOrEmpty = true, message = "Token must not be null or empty")
//                    })
//            }
    )
    public IHttpResponse<String> logout(Request request) {
        getModel().logout(request);

        return HttpResponse.ofNext(FORWARD_PAGES_FORM_LOGIN_JSP);
    }
}
