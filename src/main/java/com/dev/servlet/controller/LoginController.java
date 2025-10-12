package com.dev.servlet.controller;

import com.dev.servlet.controller.base.BaseController;
import com.dev.servlet.core.annotation.Authorization;
import com.dev.servlet.core.annotation.Controller;
import com.dev.servlet.core.annotation.Property;
import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.domain.model.enums.RequestMethod;
import com.dev.servlet.domain.service.ILoginService;
import com.dev.servlet.domain.service.IUserService;
import com.dev.servlet.domain.transfer.response.UserResponse;
import com.dev.servlet.domain.transfer.request.LoginRequest;
import com.dev.servlet.core.response.HttpResponse;
import com.dev.servlet.core.response.IHttpResponse;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;

import static com.dev.servlet.core.util.CryptoUtils.isValidToken;

@Slf4j
@NoArgsConstructor
@Singleton
@Controller("login")
public class LoginController extends BaseController {

    private static final String FORWARD_PAGES_FORM_LOGIN_JSP = "forward:pages/formLogin.jsp";
    private static final String FORWARD_PAGES_USER_FORM_CREATE_USER_JSP = "forward:pages/user/formCreateUser.jsp";

    @Inject
    private ILoginService loginService;
    @Inject
    private IUserService userService;

    @RequestMapping(value = "/registerPage", requestAuth = false)
    public IHttpResponse<String> forwardRegister() {
        return HttpResponse.<String>next(FORWARD_PAGES_USER_FORM_CREATE_USER_JSP).build();
    }

    @RequestMapping(value = "/form", requestAuth = false)
    public IHttpResponse<String> form(@Authorization String auth, @Property("homepage") String homepage) {
        String next;
        if (isValidToken(auth)) {
            next = "redirect:/" + homepage;
        } else {
            next = FORWARD_PAGES_FORM_LOGIN_JSP;
        }
        return HttpResponse.<String>next(next).build();
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST, requestAuth = false, jsonType = LoginRequest.class)
    @SneakyThrows
    public IHttpResponse<UserResponse> login(LoginRequest request, @Property("homepage") String homepage) {
        log.info("");

        UserResponse user = loginService.login(request, userService);
        if (user != null) {
            return okHttpResponse(user, "redirect:/" + homepage);
        }

        return HttpResponse.<UserResponse>newBuilder()
                .statusCode(HttpServletResponse.SC_UNAUTHORIZED)
                .error("Invalid login or password")
                .reasonText("Unauthorized")
                .next(FORWARD_PAGES_FORM_LOGIN_JSP)
                .build();
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public IHttpResponse<String> logout(@Authorization String auth) {
        loginService.logout(auth);
        return HttpResponse.<String>next(FORWARD_PAGES_FORM_LOGIN_JSP).build();
    }
}
