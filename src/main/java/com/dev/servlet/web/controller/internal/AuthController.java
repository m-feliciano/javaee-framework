package com.dev.servlet.web.controller.internal;

import com.dev.servlet.application.port.in.auth.FormUseCasePort;
import com.dev.servlet.application.port.in.auth.HomePageUseCasePort;
import com.dev.servlet.application.port.in.auth.LoginUseCasePort;
import com.dev.servlet.application.port.in.auth.LogoutUseCasePort;
import com.dev.servlet.application.port.in.auth.RegisterPageUseCasePort;
import com.dev.servlet.application.transfer.request.LoginRequest;
import com.dev.servlet.application.transfer.response.UserResponse;
import com.dev.servlet.web.controller.AuthControllerApi;
import com.dev.servlet.web.controller.internal.base.BaseController;
import com.dev.servlet.web.response.HttpResponse;
import com.dev.servlet.web.response.IHttpResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class AuthController extends BaseController implements AuthControllerApi {
    @Inject
    private LoginUseCasePort useCasePort;
    @Inject
    private FormUseCasePort formUseCasePort;
    @Inject
    private LogoutUseCasePort logoutUseCasePort;
    @Inject
    private HomePageUseCasePort homePageUseCasePort;
    @Inject
    private RegisterPageUseCasePort registerPageUseCasePort;

    public IHttpResponse<String> forwardRegister() {
        return HttpResponse.<String>next(registerPageUseCasePort.registerPage()).build();
    }

    public IHttpResponse<String> form(String auth, String homepage) {
        String next = formUseCasePort.form(auth, homepage);
        return HttpResponse.<String>next(next).build();
    }

    @SneakyThrows
    public IHttpResponse<UserResponse> login(LoginRequest request, String homepage) {
        String onSuccess = "redirect:/" + homepage;
        return useCasePort.login(request, onSuccess);
    }

    public IHttpResponse<String> logout(String auth) {
        logoutUseCasePort.logout(auth);
        return HttpResponse.<String>next(homePageUseCasePort.homePage()).build();
    }
}
