package com.dev.servlet.adapter.in.web.controller.internal;

import com.dev.servlet.adapter.in.web.annotation.Authorization;
import com.dev.servlet.adapter.in.web.annotation.Property;
import com.dev.servlet.adapter.in.web.controller.AuthControllerApi;
import com.dev.servlet.adapter.in.web.controller.internal.base.BaseController;
import com.dev.servlet.adapter.in.web.dto.HttpResponse;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.application.port.in.auth.FormUseCase;
import com.dev.servlet.application.port.in.auth.HomePageUseCase;
import com.dev.servlet.application.port.in.auth.LoginUseCase;
import com.dev.servlet.application.port.in.auth.LogoutUseCase;
import com.dev.servlet.application.port.in.auth.RefreshTokenUseCase;
import com.dev.servlet.application.port.in.auth.RegisterPageUseCase;
import com.dev.servlet.application.transfer.request.LoginRequest;
import com.dev.servlet.application.transfer.request.RefreshTokenRequest;
import com.dev.servlet.application.transfer.response.RefreshTokenResponse;
import com.dev.servlet.application.transfer.response.UserResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class AuthController extends BaseController implements AuthControllerApi {
    @Inject
    private LoginUseCase loginUseCase;
    @Inject
    private FormUseCase formUseCase;
    @Inject
    private LogoutUseCase logoutUseCase;
    @Inject
    private HomePageUseCase homePageUseCase;
    @Inject
    private RegisterPageUseCase registerPageUseCase;
    @Inject
    private RefreshTokenUseCase refreshTokenUseCase;

    @Override
    protected Class<AuthController> implementation() {
        return AuthController.class;
    }

    public IHttpResponse<String> forwardRegister() {
        return HttpResponse.<String>next(registerPageUseCase.registerPage()).build();
    }

    public IHttpResponse<String> form(@Authorization String auth, @Property("homepage") String homepage) {
        String next = formUseCase.form(auth, homepage);
        return HttpResponse.<String>next(next).build();
    }

    @SneakyThrows
    public IHttpResponse<UserResponse> login(LoginRequest request, @Property("homepage") String homepage) {
        String onSuccess = "redirect:/" + homepage;
        return loginUseCase.login(request, onSuccess);
    }

    public IHttpResponse<String> logout(@Authorization String auth) {
        logoutUseCase.logout(auth);
        return HttpResponse.<String>next(homePageUseCase.homePage()).build();
    }

    @Override
    public IHttpResponse<RefreshTokenResponse> refreshToken(RefreshTokenRequest req) {
        var res = refreshTokenUseCase.refreshToken(req.refreshToken());
        return HttpResponse.ok(res).build();
    }
}
