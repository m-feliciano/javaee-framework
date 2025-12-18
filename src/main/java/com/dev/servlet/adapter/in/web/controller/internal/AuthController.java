package com.dev.servlet.adapter.in.web.controller.internal;

import com.dev.servlet.adapter.in.web.annotation.Authorization;
import com.dev.servlet.adapter.in.web.annotation.Property;
import com.dev.servlet.adapter.in.web.controller.AuthControllerApi;
import com.dev.servlet.adapter.in.web.controller.internal.base.BaseController;
import com.dev.servlet.adapter.in.web.dto.HttpResponse;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.application.port.in.auth.FormPort;
import com.dev.servlet.application.port.in.auth.HomePagePort;
import com.dev.servlet.application.port.in.auth.LoginPort;
import com.dev.servlet.application.port.in.auth.LogoutPort;
import com.dev.servlet.application.port.in.auth.RegisterPagePort;
import com.dev.servlet.application.transfer.request.LoginRequest;
import com.dev.servlet.application.transfer.response.UserResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class AuthController extends BaseController implements AuthControllerApi {
    @Inject
    private LoginPort loginPort;
    @Inject
    private FormPort formPort;
    @Inject
    private LogoutPort logoutPort;
    @Inject
    private HomePagePort homePagePort;
    @Inject
    private RegisterPagePort registerPagePort;

    @Override
    protected Class<AuthController> implementation() {
        return AuthController.class;
    }

    public IHttpResponse<String> forwardRegister() {
        return HttpResponse.<String>next(registerPagePort.registerPage()).build();
    }

    public IHttpResponse<String> form(@Authorization String auth, @Property("homepage") String homepage) {
        String next = formPort.form(auth, homepage);
        return HttpResponse.<String>next(next).build();
    }

    @SneakyThrows
    public IHttpResponse<UserResponse> login(LoginRequest request, @Property("homepage") String homepage) {
        String onSuccess = "redirect:/" + homepage;
        return loginPort.login(request, onSuccess);
    }

    public IHttpResponse<String> logout(@Authorization String auth) {
        logoutPort.logout(auth);
        return HttpResponse.<String>next(homePagePort.homePage()).build();
    }
}
