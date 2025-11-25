package com.dev.servlet.controller.internal;

import com.dev.servlet.controller.UserControllerApi;
import com.dev.servlet.controller.base.BaseController;
import com.dev.servlet.core.response.HttpResponse;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.domain.model.User;
import com.dev.servlet.domain.records.Query;
import com.dev.servlet.domain.request.UserCreateRequest;
import com.dev.servlet.domain.request.UserRequest;
import com.dev.servlet.domain.response.UserResponse;
import com.dev.servlet.service.IUserService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@NoArgsConstructor
@Singleton
public class UserController extends BaseController implements UserControllerApi {

    public static final String REDIRECT_AUTH_FORM = "redirect:/api/v1/auth/form";

    @Inject
    private IUserService userService;

    @SneakyThrows
    @Override
    public IHttpResponse<UserResponse> update(UserRequest user, String auth) {
        UserResponse response = userService.update(user, auth);
        return newHttpResponse(204, response, redirectTo(response.getId()));
    }

    @SneakyThrows
    @Override
    public IHttpResponse<Void> delete(UserRequest user, String auth) {
        userService.delete(user, auth);
        return HttpResponse.<Void>next(forwardTo("formLogin")).build();
    }

    @SneakyThrows
    @Override
    public IHttpResponse<Void> register(UserCreateRequest user) {
        userService.register(user);
        return newHttpResponse(201, REDIRECT_AUTH_FORM);
    }

    @SneakyThrows
    @Override
    public IHttpResponse<Void> confirm(Query query) {
        String token = query.get("token");
        userService.confirmEmail(token);
        return newHttpResponse(200, REDIRECT_AUTH_FORM);
    }

    @SneakyThrows
    @Override
    public IHttpResponse<Void> changeEmail(Query query) {
        String token = query.get("token");
        userService.changeEmail(token);
        return newHttpResponse(200, REDIRECT_AUTH_FORM);
    }

    @SneakyThrows
    @Override
    public IHttpResponse<Void> resendConfirmation(User user) {
        userService.resendConfirmation(user.getId());
        return HttpResponse.<Void>next("forward:pages/formLogin.jsp").build();
    }

    @SneakyThrows
    @Override
    public IHttpResponse<UserResponse> getUserDetail(UserRequest user, String auth) {
        UserResponse response = userService.getUserDetail(user, auth);
        return okHttpResponse(response, forwardTo("formListUser"));
    }
}
