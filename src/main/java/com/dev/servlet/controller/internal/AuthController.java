package com.dev.servlet.controller.internal;

import com.dev.servlet.controller.AuthControllerApi;
import com.dev.servlet.controller.base.BaseController;
import com.dev.servlet.core.response.HttpResponse;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.domain.request.LoginRequest;
import com.dev.servlet.domain.response.UserResponse;
import com.dev.servlet.service.AuthService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@Singleton
public class AuthController extends BaseController implements AuthControllerApi {

    @Inject
    private AuthService authService;

    public IHttpResponse<String> forwardRegister() {
        return HttpResponse.<String>next(authService.registerPage()).build();
    }

    public IHttpResponse<String> form(String auth, String homepage) {
        String next = authService.form(auth, homepage);
        return HttpResponse.<String>next(next).build();
    }

    @SneakyThrows
    public IHttpResponse<UserResponse> login(LoginRequest request, String homepage) {
        return authService.login(request,  "redirect:/" + homepage);
    }

    public IHttpResponse<String> logout(String auth) {
        authService.logout(auth);
        return HttpResponse.<String>next(authService.homePage()).build();
    }
}
