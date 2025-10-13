package com.dev.servlet.controller;

import com.dev.servlet.controller.base.BaseController;
import com.dev.servlet.core.annotation.Authentication;
import com.dev.servlet.core.annotation.Controller;
import com.dev.servlet.core.annotation.Property;
import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.response.HttpResponse;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.domain.model.enums.RequestMethod;
import com.dev.servlet.domain.service.ILoginService;
import com.dev.servlet.domain.service.IUserService;
import com.dev.servlet.domain.transfer.request.LoginRequest;
import com.dev.servlet.domain.transfer.response.UserResponse;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@NoArgsConstructor
@Singleton
@Controller("login")
public class LoginController extends BaseController {

    @Inject
    private ILoginService loginService;
    @Inject
    private IUserService userService;

    @RequestMapping(value = "/registerPage", requestAuth = false)
    public IHttpResponse<String> forwardRegister() {
        return HttpResponse.<String>next("forward:pages/user/formCreateUser.jsp").build();
    }

    @RequestMapping(value = "/form", requestAuth = false)
    public IHttpResponse<String> form(@Authentication String auth, @Property("homepage") String homepage) {
        String next = loginService.form(auth, homepage);
        return HttpResponse.<String>next(next).build();
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST, requestAuth = false, jsonType = LoginRequest.class)
    @SneakyThrows
    public IHttpResponse<UserResponse> login(LoginRequest request,
                                             @Property("homepage") String homepage,
                                             @Property("loginpage") String loginPage) {
        try {
            UserResponse user = loginService.login(request, userService);
            return okHttpResponse(user, "redirect:/" + homepage);

        } catch (Exception e) {
            return HttpResponse.<UserResponse>newBuilder()
                    .statusCode(HttpServletResponse.SC_UNAUTHORIZED)
                    .error("Invalid login or password")
                    .reasonText("Unauthorized")
                    .next("forward:pages/formLogin.jsp")
                    .build();
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public IHttpResponse<String> logout(@Authentication String auth, @Property("loginpage") String loginPage) {
        loginService.logout(auth);
        return HttpResponse.<String>next(loginPage).build();
    }
}
